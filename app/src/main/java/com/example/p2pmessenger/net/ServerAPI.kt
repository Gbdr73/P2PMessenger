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

data class ServerApiResult(val result: Int,
    val answer: String
)

class ServerAPI() {
    private var _result = MutableLiveData<ServerApiResult>()
    val result: LiveData<ServerApiResult>
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

        if( answer == null )
        {
            _result.postValue(ServerApiResult(ServerUnreachable, ""))
        }
        else {
            _result.postValue(ServerApiResult(answer.toInt(), "") )
        }
    }

    suspend fun getUserIp(name: String, password: String, requestedName: String) {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect("192.168.1.3", 5080)
        var nameLen = name.length.toString()
        var reqNameLen = requestedName.length.toString()
        var ip: String = ""

        if(nameLen.length == 1)
        {
            nameLen = "0" + nameLen
        }
        if(reqNameLen.length == 1)
        {
            reqNameLen = "0" + reqNameLen
        }
        val request: String = "gip" + nameLen + name + password + reqNameLen + requestedName
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

        if( answer == null )
        {
            _result.postValue(ServerApiResult(ServerUnreachable, ""))
        }
        else {
            if( answer.length > 3 )
            {
                if( answer.take(3) == "000" )
                {
                    ip = answer.takeLast(answer.length - 3)
                    answer = "000"
                }
                else
                {
                    answer = answer.take(3)
                    +3
                }
            }
            _result.postValue(ServerApiResult(answer.toInt(), ip) )
        }
    }
}
