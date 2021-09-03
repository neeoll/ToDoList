package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

/* SUNDAY = 1, MONDAY = 2, TUESDAY = 3, WEDNESDAY = 4, THURSDAY = 5, FRIDAY = 6, SATURDAY = 7 */
class AlarmMethods {
    fun createAlarm(data: Reminder, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmReceiver::class.java)
            .putExtra("title", data.title)
        val broadcastIntent = PendingIntent.getBroadcast(
            context, data.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance()
        for (i in data.days.indices) {
            if (data.days[i]) {
                calendar.set(Calendar.DAY_OF_WEEK, i + 1)
                calendar.set(Calendar.HOUR_OF_DAY, data.hour)
                calendar.set(Calendar.MINUTE, data.minute)
                calendar.set(Calendar.SECOND, 0)

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, broadcastIntent)
            }
        }
    }

    fun toggleAlarm(data: Reminder, isChecked: Boolean, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        if (!isChecked) {
            val pendingIntent = PendingIntent.getBroadcast(
                context, data.id, intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("RECEIVER", "Alarm with ID ${data.id} cancelled successfully")
        } else {
            createAlarm(data, context)
        }
    }
}