package com.example.p2pmessenger.net

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readLine
import io.ktor.utils.io.writeByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class ServerAPI() {
    private var _result = MutableLiveData<Int>()
    val result: LiveData<Int>
        get() = _result

    companion object {
        const val Success = 0
        const val ServerUnreachable = 1
        const val UserExists = 2
        const val LoginError = 3
        const val UserNotFound = 4
    }

    suspend fun registerUser(name: String, password: String) {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect("192.168.1.3", 5080)
        var nameLen = name.length.toString()

        if(nameLen.length == 1)
        {
            nameLen = "0" + nameLen
        }
        val request: String = "reg" + nameLen + name + password
        var answer: String? = null

        val receiveChannel = socket.openReadChannel()
        val sendChannel = socket.openWriteChannel(autoFlush = true)

        try {
            sendChannel.writeByteArray(request.toByteArray(Charsets.UTF_8))
            answer = receiveChannel.readLine()
            socket.close()
        }
        catch(e: Exception )
        {
        }

        delay(2000)

        if( answer == null )
        {
            _result.postValue(ServerUnreachable)
        }
        else {
            _result.postValue(answer.toInt() )
        }
    }
}
