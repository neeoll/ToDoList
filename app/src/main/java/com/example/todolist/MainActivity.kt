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

        supportActionBar?.title = ""
        supportActionBar?.hide()
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
            fileMethods.readFile(filename, this).forEach {
                reminderList.add(0, it)
                alarmMethods.cancelAlarm(it.id, this)
                alarmMethods.createAlarm(it, this)
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
            val removedReminder = reminderList.removeAt(index)
            alarmMethods.cancelAlarm(removedReminder.id, this)
            fileMethods.writeFile(filename, this, reminderList)
            Log.d("RECEIVER", "Item removed: $removedReminder")
        } catch (e: Exception) {
            Log.e("RECEIVER", "(MainActivity) $e")
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        alarmMethods.toggleAlarm(data, isChecked, this)
        fileMethods.writeFile(filename, this, reminderList)
    }
}