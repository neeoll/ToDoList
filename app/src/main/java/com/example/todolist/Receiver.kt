package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

class Receiver: BroadcastReceiver() {
    private val channelId = "i.apps.notifications"
    private lateinit var notificationChannel: NotificationChannel

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("RECEIVER", "Receiver: ${Date()}")

        val notificationIntent = Intent(context, MainActivity::class.java)
        Log.d("RECEIVER", "NotificationIntent: $notificationIntent")

        val stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(MainActivity::class.java)
            .addNextIntent(notificationIntent)
        Log.d("RECEIVER", "StackBuilder: $stackBuilder")

        val pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.d("RECEIVER", "PendingIntent: $pendingIntent")


        val compatBuilder = NotificationCompat.Builder(context!!, channelId)
        Log.d("RECEIVER", "Builder: $compatBuilder")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("RECEIVER", "NotificationManager: $notificationManager")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = NotificationChannel(
                channelId,
                "Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "This is default channel used for all other notifications"
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = compatBuilder
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText(intent?.getStringExtra("title"))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent).build()
        Log.d("RECEIVER", "Notification: $notification")

        notificationManager.notify(100, notification)
    }
}