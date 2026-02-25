package com.example.p2pmessenger

import android.app.Application
import com.example.p2pmessenger.data.DialogData
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class P2PMsgApp: Application() {

    override fun onCreate() {
        super.onCreate()
/*
        for (i in 1..10) {
            chatsListStore.add( DialogData("Name $i", mutableListOf<String>("Message $i"), "") )
        }*/
    }
}

val chatsListStore = mutableListOf<DialogData>()

fun getChatsList(): MutableList<DialogData> {
    return chatsListStore
}