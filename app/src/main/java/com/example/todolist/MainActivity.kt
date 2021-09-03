package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.home.HomeFragment
import com.example.todolist.newreminder.NewReminderFragment
import java.util.*


// TODO: delete test, tasks, taskList, file
class MainActivity : AppCompatActivity(), NewReminderFragment.Communicator, HomeFragment.HomeCallback {

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
            }

            val bundle = Bundle()
            bundle.putSerializable("data", reminderList)

            val transaction = this.supportFragmentManager.beginTransaction()
            val homeFragment = HomeFragment(this, filename)
            homeFragment.arguments = bundle
            transaction.replace(R.id.fragment_container, homeFragment).commit()
        }
    }

    override fun sendReminderData(data: Reminder) {
        val bundle = Bundle()

        reminderList.add(data)
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
            Log.d("RECEIVER", "Item removed: $temp")
        } catch (e: Exception) {
            Log.e("RECEIVER", "(MainActivity) $e")
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        AlarmMethods().toggleAlarm(data, isChecked, this)
    }
}