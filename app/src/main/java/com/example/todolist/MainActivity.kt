package com.example.todolist

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todolist.home.HomeFragment
import java.io.*
import kotlin.collections.ArrayList

// TODO: delete test, tasks, taskList
class MainActivity : AppCompatActivity(), Communicator, HomeFragment.HomeCallback {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: NotificationCompat.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

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

    private fun checkIfDataExists(): Boolean {
        return fileExists(filename) == true
    }

    private fun fileExists(name: String): Boolean? {
        val file: File? = applicationContext.getFileStreamPath(name)
        return file?.exists()
    }

    override fun sendTaskData(data: Task) {
        val bundle = Bundle()

        pushNotification(data)
        taskList.add(data)
        bundle.putSerializable("data", taskList)

        val transaction = this.supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment(this)
        homeFragment.arguments = bundle
        transaction.replace(R.id.fragment_container, homeFragment).commit()
    }

    private fun pushNotification(data: Task) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, LauncherActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baseline_delete_outline_24)
                .setContentIntent(pendingIntent)
                .setContentTitle(data.title)
                .setContentText(data.description)
        } else {
            builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baseline_delete_outline_24)
                .setContentIntent(pendingIntent)
                .setContentTitle(data.title)
                .setContentText(data.description)
        }
        notificationManager.notify(1234, builder.build())
    }

    override fun removeAtIndex(index: Int) {
        taskList.removeAt(index)
    }
}