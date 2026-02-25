package com.example.p2pmessenger.ui

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2pmessenger.net.ServerAPI
import com.example.p2pmessenger.net.ServerApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.security.MessageDigest
import javax.inject.Inject
import java.io.File
import java.io.FileWriter


@HiltViewModel
class RegistrationViewModel @Inject constructor(): ViewModel() {
    private var _canContinue = MutableLiveData<Boolean>(false)
    val canContinue: LiveData<Boolean>
        get() = _canContinue
    private var _errorEmptyName = MutableLiveData<Boolean>()
    val errorEmptyName: LiveData<Boolean>
        get() = _errorEmptyName
    private var _errorEmptyPassword = MutableLiveData<Boolean>()
    val errorEmptyPassword: LiveData<Boolean>
        get() = _errorEmptyPassword
    private var _errorServer = MutableLiveData<String>()
    val errorServer: LiveData<String>
        get() = _errorServer

    private val serverApi = ServerAPI()
    private var userName: String = ""
    private var userPassword: String = ""

    private var lifecycleOwner: Boolean = false
    private var context: Context? = null

    fun isUserRegistered(): Boolean {
        var result = false

        if( userName.isNotEmpty() && userPassword.isNotEmpty() )
        {
            result = true
        }

        return result
    }
    fun validateData(owner: LifecycleOwner) {
        var successful = checkEmptyFields()

        _canContinue.value = false

        if (successful == false){
            return
        }
        if( !lifecycleOwner ) {
            lifecycleOwner = true
            serverApi.result.observe(owner) {
                    val res: ServerApiResult = it
                    when (res.result) {
                        ServerAPI.Success -> {
                            saveUser()
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
                            _errorServer.value = "ServerUnreachable!"
                        }
                    }
                }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                serverApi.registerUser(userName, userPassword)
            }
        }
//        _errorServerUnreachable.value = true
//        _canContinue.value = false
    }

    fun setContext(context: Context)
    {
        this.context = context
    }
    fun setName(name: String) {
        userName = name
    }

    fun setPassword(password: String) {
        userPassword = sha256(password)
    }

    fun loadUser() {
        try {
            val folderPath = context?.filesDir
            val file = File(folderPath.toString() +"/" + "userinfo.dat")

            val list = file.readLines(Charsets.UTF_8)

            if( list.size == 2 ) {
                userName = list[0]
                userPassword = list[1]
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
    private fun saveUser() {
        try {
            val folderPath = context?.filesDir
            val file = File(folderPath.toString() +"/" + "userinfo.dat")
            val writer = FileWriter(file)
            writer.write(userName + "\n" + userPassword)
            writer.close()
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
    private fun checkEmptyFields(): Boolean{
        var successful = true

        if (userName == ""){
            _errorEmptyName.value = true
            successful = false
        } else{
            _errorEmptyName.value = false
        }

        if (userPassword == ""){
            _errorEmptyPassword.value = true
            successful = false
        } else {
            _errorEmptyPassword.value = false
        }

        return successful
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.fold("") { str, byte -> str + "%02x".format(byte) }
    }
}
