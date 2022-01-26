package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.home.HomeFragment
import com.example.todolist.util.AlarmUtil
import com.example.todolist.util.DatabaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), Communicator, HomeFragment.HomeCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var reminderList: ArrayList<Reminder> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""

        if (BuildConfig.DEBUG) {
            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.database.useEmulator("10.0.2.2", 9000)
        }

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        if (DatabaseUtil.getUid(this) == "") {
            DatabaseUtil.setUid(this, auth.currentUser?.uid.toString())
        }

        Firebase.database.setPersistenceEnabled(true)
        database = Firebase.database.getReference("${DatabaseUtil.getUid(this)}/reminders")
        checkDatabase()
    }

    private fun checkDatabase() {
        database.get().addOnSuccessListener { database ->
            if (database.hasChildren()) {
                try {
                    database.children.forEach {
                        val newReminder = it.getValue(Reminder::class.java)!!
                        AlarmUtil.cancelAlarm(newReminder.alarmId, this)
                        reminderList.add(newReminder)
                        AlarmUtil.createAlarm(newReminder, this)
                    }
                    transitionToHomeFragment(true)
                } catch (e: Exception) {
                    Log.d(TAG, "$e")
                    transitionToHomeFragment(false)
                    Toast.makeText(
                        this@MainActivity, "Failed to get stored data", Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                transitionToHomeFragment(false)
            }
        }.addOnFailureListener {
            Log.e(TAG, "$it")
            transitionToHomeFragment(false)
            Toast.makeText(
                this@MainActivity, "Failed to get stored data", Toast.LENGTH_LONG
            ).show()
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

        transaction.replace(binding.fragmentContainer.id, homeFragment).commit()
    }

    override fun receiveReminderData(data: Reminder, action: String) {
        when (action) {
            "create" -> {
                reminderList.add(data)
                DatabaseUtil.writeData(database, data)
            }
            "update" -> {
                for (i in reminderList.indices) {
                    if (reminderList[i].alarmId == data.alarmId) {
                        reminderList[i] = data
                    }
                }
                DatabaseUtil.updateData(database, data)
            }
        }

        AlarmUtil.createAlarm(data, this)
        transitionToHomeFragment(true)
    }

    override fun removeAtIndex(index: Int) {
        try {
            val removedReminder = reminderList.removeAt(index)
            AlarmUtil.cancelAlarm(removedReminder.alarmId, this)
            DatabaseUtil.removeData(database, removedReminder.pushId)
            Log.d(TAG, "Item removed: $removedReminder")
        } catch (e: Exception) {
            Log.e(TAG, "Error:", e)
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        data.isActive = isChecked
        AlarmUtil.toggleAlarm(data, this)
        DatabaseUtil.updateData(database, data)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}