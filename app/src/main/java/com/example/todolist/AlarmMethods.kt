package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.todolist.receiver.AlarmReceiver
import java.util.*

// TODO: Remove log statements
/* SUNDAY = 1, MONDAY = 2, TUESDAY = 3, WEDNESDAY = 4, THURSDAY = 5, FRIDAY = 6, SATURDAY = 7 */
class AlarmMethods {
    private val weekInMillis: Long = 604800000

    fun createAlarm(data: Reminder, context: Context) {
        if (!data.isActive) { return }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmReceiver::class.java)
            .putExtra("title", data.title)
            .putExtra("id", data.id)

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
    }

    fun toggleAlarm(data: Reminder, isChecked: Boolean, context: Context) {
        when (isChecked) {
            true -> { cancelAlarm(data.id, context) }
            false -> { createAlarm(data, context) }
        }
    }

    fun cancelAlarm(dataId: Int, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, dataId, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("ALARM", "Alarm with ID $dataId cancelled successfully")
    }

    private fun getDate(milliseconds: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS")
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.timeInMillis)
    }
}