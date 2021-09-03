package com.example.todolist

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileMethods() {

    fun exists(filename:String, context: Context): Boolean? {
        val file: File? = context.getFileStreamPath(filename)
        return file?.exists()
    }

    fun readFile(filename: String, context: Context?): ArrayList<Reminder> {
        val reminderList: ArrayList<Reminder> = arrayListOf()

        val fileInputStream = context?.openFileInput(filename)
        val objectInputStream = ObjectInputStream(fileInputStream)

        val tempList = objectInputStream.readObject() as java.util.ArrayList<Reminder>
        for (item in tempList) {
            reminderList.add(0, item)
        }

        return reminderList
    }

    fun writeFile(filename: String, context: Context?, reminderList: ArrayList<Reminder>) {
        val fileOutputStream = context?.openFileOutput(filename,
            AppCompatActivity.MODE_PRIVATE
        )
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(reminderList)
        fileOutputStream?.close()

        Log.e("TASKS", "contents written to file")
    }
}