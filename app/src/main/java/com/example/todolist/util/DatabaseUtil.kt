package com.example.todolist.util


import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.todolist.Reminder
import com.google.firebase.database.*
import java.lang.Exception

class DatabaseUtil {
    companion object {

        private const val UID = "com.example.todolist.uid"

        fun getUid(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(UID, "")!!
        }

        fun setUid(context: Context, uid: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(UID, uid)
            editor.apply()
        }

        fun writeData(reminderRef: DatabaseReference, reminder: Reminder): String? {
            val newReminderRef = reminderRef.push()
            reminder.pushId = newReminderRef.key!!
            newReminderRef.setValue(reminder)

            return newReminderRef.key
        }

        fun updateData(reminderRef: DatabaseReference, reminder: Reminder) {
            try {
                val updateRef = reminderRef.child(reminder.pushId)
                updateRef.setValue(reminder)
                Log.d(TAG, "Reminder updated")
            } catch (e: Exception) {
                Log.e(TAG, "Error:", e)
            }
        }

        fun removeData(reminderRef: DatabaseReference, key: String) {
            try {
                val removeRef = reminderRef.child(key)
                removeRef.removeValue()
                Log.d(TAG, "Reminder removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error:", e)
            }

        }

        private const val TAG = "DatabaseUtil"
    }
}