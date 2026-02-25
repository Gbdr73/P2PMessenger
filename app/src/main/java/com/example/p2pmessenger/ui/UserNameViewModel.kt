package com.example.p2pmessenger.ui

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2pmessenger.chatsListStore
import com.example.p2pmessenger.data.DialogData
import com.example.p2pmessenger.getChatsList
import com.example.p2pmessenger.net.ServerAPI
import com.example.p2pmessenger.net.ServerApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(): ViewModel() {
    private var _canContinue = MutableLiveData<Boolean>(false)
    val canContinue: LiveData<Boolean>
        get() = _canContinue

    private var _errorServer = MutableLiveData<String>()
    val errorServer: LiveData<String>
        get() = _errorServer

    private var lifecycleOwner: Boolean = false
    private val serverApi = ServerAPI()
    private var userName: String = ""
    private var ownerName: String = ""
    private var ownerPassword: String = ""
    private var context: Context? = null

    fun setName(name: String) {
        userName = name
    }

    fun setContext(context: Context)
    {
        this.context = context
    }

    private fun newChat(ip: String) {
        val chatsList = getChatsList()
        chatsList.add(DialogData(userName, mutableListOf<String>(), ip))
    }
    fun addChat(owner: LifecycleOwner) :Boolean {
        if( userName.isNullOrEmpty() )
        {
            return false
        }

        if( !lifecycleOwner ) {
            lifecycleOwner = true
            serverApi.result.observe(owner) {
                val res: ServerApiResult = it
                when (res.result) {
                    ServerAPI.Success -> {
                        newChat(res.answer)
                        _canContinue.value = true
                    }
                    ServerAPI.ServerUnreachable -> {
                        _errorServer.value = "ServerUnreachable!"
                    }
                    ServerAPI.UserExists -> {
                        _errorServer.value = "Username already exists, Try another!"
                    }
                    ServerAPI.LoginError -> {
                        _errorServer.value = "ServerUnreachable!"
                    }
                    ServerAPI.UserNotFound -> {
                        _errorServer.value = "User $userName not found!"
                    }
                }
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                serverApi.getUserIp(ownerName, ownerPassword, userName)
            }
        }
        return true
    }

    fun loadOwner() {
        try {
            val folderPath = context?.filesDir
            val file = File(folderPath.toString() +"/" + "userinfo.dat")

            val list = file.readLines(Charsets.UTF_8)

            if( list.size == 2 ) {
                ownerName = list[0]
                ownerPassword = list[1]
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
}
