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
import com.example.todolist.editreminder.EditReminderFragment
import com.example.todolist.newreminder.NewReminderFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.reminder_card.*

// TODO: Remove log statements
class HomeFragment(listener: HomeCallback) : Fragment(),
    RecyclerAdapter.RecyclerCallback {

    private var reminderList: ArrayList<Reminder> = arrayListOf()
    private var communicatorData: ArrayList<Reminder> = arrayListOf()
    private val homeListener: HomeCallback = listener

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

        reminderList.clear()

        if (arguments != null) {
            communicatorData = arguments?.getSerializable("data") as ArrayList<Reminder>
            for (item in communicatorData) {
                insertReminder(Reminder(
                    item.id, item.title, item.hour, item.minute, item.days, item.recurring, item.isActive)
                )
            }
        }

        val itemTouchCallback = object: SwipeToDelete(0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d("SWIPE", "$direction")
                val position = viewHolder.adapterPosition
                val temp = reminderList.removeAt(position)

                val layout: RecyclerView = this@HomeFragment.requireView().findViewById(R.id.reminder_recycler)
                Snackbar.make(layout, "Reminder Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        reminderList.add(position, temp)
                        layout.adapter?.notifyItemInserted(position)
                    }
                    .addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                removeAtIndex(position)
                                layout.adapter?.notifyItemRemoved(position)
                            }
                        }
                    }).show()
            }
        }

        view.reminder_recycler.apply {
            layoutManager = LinearLayoutManager(
                activity, LinearLayoutManager.VERTICAL, false)
            adapter = RecyclerAdapter(reminderList, this@HomeFragment, this)

            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }

        view.add_reminder.setOnClickListener {
            val newReminderFragment = NewReminderFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager
            val transaction = supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, newReminderFragment)
                .addToBackStack("homeFragment")
                .commit()
        }

        return view
    }

    private fun insertReminder(reminder: Reminder) {
        reminderList.add(0, reminder)
    }

    // TODO: Remove log statements
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

    override fun openEditFragment(data: Reminder) {
        val editReminderFragment = EditReminderFragment(data)
        val supportFragmentManager = requireActivity().supportFragmentManager
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_container, editReminderFragment)
            .addToBackStack("homeFragment")
            .commit()
    }
}