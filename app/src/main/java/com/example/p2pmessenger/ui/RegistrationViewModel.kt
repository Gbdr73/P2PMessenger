package com.example.p2pmessenger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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

    private var userName: String = ""
    private var userPassword: String = ""

    fun validateData() {
        var successful = checkEmptyFields()

        if (successful == false){
            _canContinue.value = false
            return
        }
        _canContinue.value = true
    }

    fun setName(name: String) {
        userName = name
    }

    fun setPassword(password: String) {
        userPassword = password
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
}
