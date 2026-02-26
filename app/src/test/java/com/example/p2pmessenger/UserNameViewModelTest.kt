package com.example.p2pmessenger

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.p2pmessenger.ui.UsernameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class UserNameViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    val lifecycleOwner: LifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
    private val viewModel: UsernameViewModel = UsernameViewModel()

    @Before
    fun before(){
        Dispatchers.setMain(testDispatcher)
        println("Начинается тест")
    }
    @After
    fun after(){
        Dispatchers.resetMain()
        println("Тест закончился")
    }

    @Test
    fun `empty userName returns error`() {
        runTest {
            val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))
            lifecycle.markState(Lifecycle.State.RESUMED)
            Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            val actual = viewModel.addChat(lifecycleOwner) ?: throw RuntimeException("errorEmptyName == null")
            assertFalse(actual)
        }
    }
    @Test
    fun `valid userName returns success`() {
        runTest {
            val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))
            lifecycle.markState(Lifecycle.State.RESUMED)
            Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            viewModel.setName("User1")
            val actual = viewModel.addChat(lifecycleOwner) ?: throw RuntimeException("errorEmptyName == null")
            assertTrue(actual)
        }
    }
}