package com.example.todolist.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.todolist.Reminder
import com.example.todolist.receiver.AlarmReceiver
import com.example.todolist.receiver.BootReceiver
import java.util.*

class AlarmUtil {
    companion object {
        private const val weekInMillis: Long = 604800000

        fun createAlarm(data: Reminder, context: Context) {
            if (!data.isActive) { return }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val notificationIntent = Intent(context, AlarmReceiver::class.java)
                .putExtra("title", data.title)
                .putExtra("id", data.pushId)

            val broadcastIntent = PendingIntent.getBroadcast(
                context, data.alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance()
            for (i in data.days.indices) {
                if (data.days[i]) {
                    calendar.apply {
                        set(Calendar.DAY_OF_WEEK, i + 1)
                        set(Calendar.HOUR_OF_DAY, data.hour)
                        set(Calendar.MINUTE, data.minute)
                        set(Calendar.SECOND, 0)
                    }

                    if (System.currentTimeMillis() >= calendar.timeInMillis) {
                        calendar.add(Calendar.DAY_OF_YEAR, 7)
                    }

                    if (data.recurring) {
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            weekInMillis,
                            broadcastIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            broadcastIntent
                        )
                    }
                }
            }

            Log.d(TAG, "Alarms Created")
        }

        fun toggleAlarm(data: Reminder, context: Context) {
            when (data.isActive) {
                true -> { cancelAlarm(data.alarmId, context) }
                false -> { createAlarm(data, context) }
            }
        }

        fun cancelAlarm(alarmId: Int, context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm with ID $alarmId cancelled successfully")
        }

        private const val TAG = "AlarmUtil"
    }
}