package com.example.todolist.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.todolist.Reminder
import java.io.File
import java.io.ObjectInputStream
import java.util.*
import kotlin.collections.ArrayList

// TODO: Remove log statements
class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d("BOOT", "Device booted")

            if (exists("reminders", context) == true) {
                val fileData = readFile("reminders", context)
                for (item in fileData) {
                    createAlarm(item, context)
                }
                Log.d("BOOT", "$fileData")
            }
        }
    }

    private fun createAlarm(data: Reminder, context: Context) {
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
                        60000,
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

    private fun exists(filename:String, context: Context): Boolean? {
        return context.getFileStreamPath(filename).exists()
    }

    private fun readFile(filename: String, context: Context?): ArrayList<Reminder> {
        val fileInputStream = context?.openFileInput(filename)
        val objectInputStream = ObjectInputStream(fileInputStream)

        return objectInputStream.readObject() as java.util.ArrayList<Reminder>
    }
}