package com.example.todolist.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

// TODO: Remove log statements
class BootService: Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("BOOT", "Service started")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}