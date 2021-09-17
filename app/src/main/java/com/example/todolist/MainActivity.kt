package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.home.HomeFragment
import java.util.*

// TODO: Remove log statements
class MainActivity : AppCompatActivity(), Communicator, HomeFragment.HomeCallback {

    private val fileMethods = FileMethods()
    private val alarmMethods = AlarmMethods()
    private var reminderList: ArrayList<Reminder> = arrayListOf()
    private lateinit var filename: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filename = getString(R.string.filename)

        checkForFile()
    }

    private fun checkForFile() {
        if (fileMethods.exists(filename, this) == false) {
            Log.d("MAIN", "Doesn't Exist")
            transitionToHomeFragment(false)
        } else {
            Log.d("MAIN", "Exists")

            for (item in fileMethods.readFile(filename, this)) {
                reminderList.add(0, item)
                alarmMethods.cancelAlarm(item.id, this)
                alarmMethods.createAlarm(item, this)
            }

            transitionToHomeFragment(true)
        }
    }

    private fun transitionToHomeFragment(hasBundle: Boolean) {
        val transaction = this.supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment(this)

        if (hasBundle) {
            val bundle = Bundle()
            bundle.putSerializable("data", reminderList)
            homeFragment.arguments = bundle
        }

        transaction.replace(R.id.fragment_container, homeFragment).commit()
    }

    override fun receiveReminderData(data: Reminder, action: String) {
        when (action) {
            "create" -> { reminderList.add(data) }
            "update" -> {
                for (i in reminderList.indices) {
                    if (reminderList[i].id == data.id) {
                        reminderList[i] = data
                    }
                }
            }
        }

        fileMethods.writeFile(filename, this, reminderList)
        alarmMethods.createAlarm(data, this)
        transitionToHomeFragment(true)
    }

    override fun removeAtIndex(index: Int) {
        try {
            val temp = reminderList.removeAt(index)
            fileMethods.writeFile(filename, this, reminderList)
            Log.d("RECEIVER", "Item removed: $temp")
        } catch (e: Exception) {
            Log.e("RECEIVER", "(MainActivity) $e")
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        alarmMethods.toggleAlarm(data, isChecked, this)
        fileMethods.writeFile(filename, this, reminderList)
    }

    override fun onBackPressed() {
        Log.d("MAIN", "Back Pressed")
        Log.d("MAIN", "${reminderList.size}")
        super.onBackPressed()
    }
}