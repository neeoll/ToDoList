package com.example.todolist.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.todolist.BuildConfig
import com.example.todolist.Reminder
import com.example.todolist.util.AlarmUtil
import com.example.todolist.util.DatabaseUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Remove log statements
class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d(TAG, "Device booted")

            if (BuildConfig.DEBUG) {
                Firebase.auth.useEmulator("10.0.2.2", 9099)
                Firebase.database.useEmulator("10.0.2.2", 9000)
            }

            Firebase.database.setPersistenceEnabled(true)
            val database = Firebase.database.getReference("${DatabaseUtil.getUid(context)}/reminders")
            database.get().addOnSuccessListener {
                if (it.hasChildren()) {
                    try {
                        it.children.forEach { snapshot ->
                            AlarmUtil.createAlarm(snapshot.getValue(Reminder::class.java)!!, context)
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "$e")
                        Toast.makeText(
                            context, "Failed to get stored data", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "FailureListener:", it)
            }
        }
    }

    private fun createAlarm(data: Reminder, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmReceiver::class.java)
            .putExtra("title", data.title)
            .putExtra("id", data.alarmId)

        val broadcastIntent = PendingIntent.getBroadcast(
            context, data.alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
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

    companion object {
        const val TAG = "BootReceiver"
    }
}