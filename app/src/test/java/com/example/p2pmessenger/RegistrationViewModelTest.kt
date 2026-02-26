package com.example.p2pmessenger

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.p2pmessenger.ui.RegistrationViewModel
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

class RegistrationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    val lifecycleOwner: LifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
    private val viewModel: RegistrationViewModel = RegistrationViewModel()

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

            viewModel.validateData(lifecycleOwner)
            val actual = viewModel.errorEmptyName.value ?: throw RuntimeException("errorEmptyName == null")
            assertTrue(actual)
        }
    }
    @Test
    fun `valid userName returns success`() {
        runTest {
            val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))
            lifecycle.markState(Lifecycle.State.RESUMED)
            Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            viewModel.setName("User1")
            viewModel.validateData(lifecycleOwner)
            val actual = viewModel.errorEmptyName.value ?: throw RuntimeException("errorEmptyName == null")
            assertFalse(actual)
        }
    }
    @Test
    fun `empty userPassword returns error`() {
        runTest {
            val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))
            lifecycle.markState(Lifecycle.State.RESUMED)
            Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            viewModel.setName("User1")
            viewModel.validateData(lifecycleOwner)
            val actual = viewModel.errorEmptyPassword.value ?: throw RuntimeException("errorEmptyName == null")
            assertTrue(actual)
        }
    }
    @Test
    fun `valid userPassword returns success`() {
        runTest {
            val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))
            lifecycle.markState(Lifecycle.State.RESUMED)
            Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            viewModel.setName("User1")
            viewModel.setPassword("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")
            viewModel.validateData(lifecycleOwner)
            val actual = viewModel.errorEmptyPassword.value ?: throw RuntimeException("errorEmptyName == null")
            assertFalse(actual)
        }
    }
}