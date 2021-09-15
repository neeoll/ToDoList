package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.home.HomeFragment
import java.util.*

// TODO: Remove log statements
// TODO: delete test, tasks, taskList, file
class MainActivity : AppCompatActivity(), Communicator, HomeFragment.HomeCallback {

    private var reminderList: ArrayList<Reminder> = arrayListOf()
    private lateinit var filename: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filename = getString(R.string.filename)

        transitionToHomeFragment()
    }

    private fun transitionToHomeFragment() {
        if (FileMethods().exists(filename, this) == false) {
            Log.d("MAIN", "Doesn't Exist")
            val homeFragment = HomeFragment(this, filename)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment)
                .commit()
        } else {
            Log.d("MAIN", "Exists")

            for (item in FileMethods().readFile(filename, this)) {
                reminderList.add(0, item)
                AlarmMethods().cancelAlarm(item.id, this)
                AlarmMethods().createAlarm(item, this)
            }

            val bundle = Bundle()
            bundle.putSerializable("data", reminderList)

            val transaction = this.supportFragmentManager.beginTransaction()
            val homeFragment = HomeFragment(this, filename)
            homeFragment.arguments = bundle
            transaction.replace(R.id.fragment_container, homeFragment).commit()
        }
    }

    override fun createReminderData(data: Reminder) {
        val bundle = Bundle()

        reminderList.add(data)
        FileMethods().writeFile(filename, this, reminderList)
        AlarmMethods().createAlarm(data, this)
        bundle.putSerializable("data", reminderList)

        val transaction = this.supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment(this, filename)
        homeFragment.arguments = bundle
        transaction.replace(R.id.fragment_container, homeFragment).commit()
    }

    override fun updateReminderData(data: Reminder) {
        val bundle = Bundle()

        for (i in reminderList.indices) {
            if (reminderList[i].id == data.id) {
                reminderList[i] = data
            }
        }

        FileMethods().writeFile(filename, this, reminderList)
        AlarmMethods().createAlarm(data, this)
        bundle.putSerializable("data", reminderList)

        val transaction = this.supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment(this, filename)
        homeFragment.arguments = bundle
        transaction.replace(R.id.fragment_container, homeFragment).commit()
    }

    override fun removeAtIndex(index: Int) {
        try {
            val temp = reminderList.removeAt(index)
            FileMethods().writeFile(filename, this, reminderList)
            Log.d("RECEIVER", "Item removed: $temp")
        } catch (e: Exception) {
            Log.e("RECEIVER", "(MainActivity) $e")
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        AlarmMethods().toggleAlarm(data, isChecked, this)
        FileMethods().writeFile(filename, this, reminderList)
    }
}