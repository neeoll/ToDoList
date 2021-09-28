package com.example.todolist

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

// TODO: Remove log statements
class FileMethods() {

    fun exists(filename:String, context: Context): Boolean? {
        return context.getFileStreamPath(filename).exists()
    }

    fun readFile(filename: String, context: Context?): ArrayList<Reminder> {
        val fileInputStream = context?.openFileInput(filename)
        val objectInputStream = ObjectInputStream(fileInputStream)

        return objectInputStream.readObject() as java.util.ArrayList<Reminder>
    }

    fun writeFile(filename: String, context: Context?, reminderList: ArrayList<Reminder>) {
        val fileOutputStream = context?.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)

        objectOutputStream.writeObject(reminderList)
        fileOutputStream?.close()

        Log.d("TASKS", "contents written to file")
    }
}