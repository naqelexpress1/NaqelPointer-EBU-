package com.naqelexpress.naqelpointer

import android.app.Application
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.rooms.Room

class AppController(): Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
       // lateinit var currentUser:CurrentUser
       // lateinit var room:Room
    }
}