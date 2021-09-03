package com.example.todolist.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.*
import com.example.todolist.newreminder.NewReminderFragment
import com.example.todolist.FileMethods
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment(listener: HomeCallback, file: String) : Fragment(),
    RecyclerAdapter.RecyclerCallback {

    private var reminderList: ArrayList<Reminder> = arrayListOf()
    private var communicatorData: ArrayList<Reminder> = arrayListOf()
    private val homeListener: HomeCallback = listener
    private val filename: String = file

    interface HomeCallback {
        fun removeAtIndex(index: Int)
        fun switchAlarm(data: Reminder, isChecked: Boolean)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        if (arguments != null) {
            communicatorData = arguments?.getSerializable("data") as ArrayList<Reminder>
            for (item in communicatorData) {
                insertReminder(Reminder(
                    item.id, item.title, item.hour, item.minute, item.days, item.recurring, item.isActive)
                )
            }
        }

        /*val swipeToDelete = object: SwipeToDelete(0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d("MAIN", "${viewHolder.adapterPosition}")
                homeListener.removeAtIndex(viewHolder.adapterPosition)
            }
        }

        val helper = ItemTouchHelper(swipeToDelete)*/

        view.reminder_recycler.apply {
            layoutManager = LinearLayoutManager(
                activity, LinearLayoutManager.VERTICAL, false)
            adapter = RecyclerAdapter(reminderList, this@HomeFragment, this)
            // helper.attachToRecyclerView(this)
        }

        view.add_reminder.setOnClickListener {
            val newTaskFragment = NewReminderFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager
            val transaction = supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, newTaskFragment).commit()
        }

        return view
    }

    private fun insertReminder(reminder: Reminder) {
        reminderList.add(0, reminder)
    }

    override fun onStop() {
        super.onStop()
        context?.let { FileMethods().writeFile(filename, it, reminderList) }
    }

    override fun removeAtIndex(index: Int) {
        try {
            val temp = reminderList.removeAt(index)
            homeListener.removeAtIndex(index)
            Log.d("RECEIVER", "Item removed: $temp")
        } catch (e: Exception) {
            Log.e("RECEIVER", "(HomeFragment) $e")
        }
    }

    override fun switchAlarm(data: Reminder, isChecked: Boolean) {
        homeListener.switchAlarm(data, isChecked)
    }
}