package com.example.todolist

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.home.HomeFragment
import java.io.File
import java.io.ObjectInputStream
import java.util.*


// TODO: delete test, tasks, taskList
class MainActivity : AppCompatActivity(), Communicator, HomeFragment.HomeCallback {
    private lateinit var alarmManager: AlarmManager

    private var taskList: ArrayList<Task> = arrayListOf()
    private val filename: String = "file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (fileExists(filename) == false) {
            val homeFragment = HomeFragment(this)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment)
                .commit()
        } else {
            val fileInputStream = applicationContext.openFileInput(filename)
            val objectInputStream = ObjectInputStream(fileInputStream)

            val tempList = objectInputStream.readObject() as ArrayList<Task>
            for (item in tempList) {
                taskList.add(0, item)
            }

            val bundle = Bundle()
            bundle.putSerializable("data", taskList)

            val transaction = this.supportFragmentManager.beginTransaction()
            val homeFragment = HomeFragment(this)
            homeFragment.arguments = bundle
            transaction.replace(R.id.fragment_container, homeFragment).commit()
        }
    }

    private fun fileExists(name: String): Boolean? {
        val file: File? = applicationContext.getFileStreamPath(name)
        return file?.exists()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun sendTaskData(data: Task) {
        val bundle = Bundle()

        taskList.add(data)
        createNotificationIntent(data)
        bundle.putSerializable("data", taskList)

        val transaction = this.supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment(this)
        homeFragment.arguments = bundle
        transaction.replace(R.id.fragment_container, homeFragment).commit()
    }

    /* SUNDAY = 1, MONDAY = 2, TUESDAY = 3, WEDNESDAY = 4, THURSDAY = 5, FRIDAY = 6, SATURDAY = 7 */
    private fun createNotificationIntent(data: Task) {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(this, Receiver::class.java)
            .putExtra("title", data.title)
        val broadcastIntent = PendingIntent.getBroadcast(
            this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, data.hour)
        calendar.set(Calendar.MINUTE, data.minute)
        calendar.set(Calendar.SECOND, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, broadcastIntent)
        Log.d("RECEIVER", "Broadcaster: ${Date()}")
    }

    override fun removeAtIndex(index: Int) {
        taskList.removeAt(index)
    }
}