package com.example.todolist.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.*
import com.example.todolist.editreminder.EditReminderFragment
import com.example.todolist.newreminder.NewReminderFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.reminder_card.*


// TODO: Remove log statements
class HomeFragment(listener: HomeCallback) : Fragment(),
    RecyclerAdapter.RecyclerCallback {

    private var reminderList: ArrayList<Reminder> = arrayListOf()
    private val homeListener: HomeCallback = listener
    private var actionMode: ActionMode? = null

    private val actionModeCallback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val tempList: ArrayList<Reminder> = arrayListOf()

            reminderList.forEach {
                if (it.selected) {
                    tempList.add(it)
                }
            }

            when (item?.itemId) {
                R.id.action_delete -> {
                    tempList.forEach {
                        if (reminderList.contains(it)) {
                            requireView().reminder_recycler.adapter?.notifyItemRemoved(reminderList.indexOf(it))
                            removeAtIndex(reminderList.indexOf(it))
                        }
                    }

                    mode?.finish()
                }
                R.id.action_edit -> {
                    if (tempList.size == 1) {
                        openFragment("editReminder", tempList[0])
                        mode?.finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Must have exactly 1 item to edit",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            reminderList.forEach { it.selected = false }
            actionMode = null
        }
    }

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
            reminderList.clear()
            val communicatorData = arguments?.getSerializable("data") as ArrayList<*>
            communicatorData.forEach {
                reminderList.add(0, it as Reminder)
            }
        }

        view.reminder_recycler.apply {
            layoutManager = LinearLayoutManager(
                activity, LinearLayoutManager.VERTICAL, false)
            adapter = RecyclerAdapter(reminderList, this@HomeFragment)
        }

        view.add_reminder.setOnClickListener {
            openFragment("newReminder")
        }

        return view
    }

    private fun openFragment(name: String, reminder: Reminder? = null) {
        val supportFragmentManager = requireActivity().supportFragmentManager
        val transaction = supportFragmentManager.beginTransaction()

        when (name) {
            "newReminder" -> {
                val fragment = NewReminderFragment()
                transaction.replace(R.id.fragment_container, fragment)
                    .addToBackStack("homeFragment")
                    .commit()
            }
            "editReminder" -> {
                actionMode?.finish()
                val fragment = EditReminderFragment(reminder!!)
                transaction.replace(R.id.fragment_container, fragment)
                    .addToBackStack("homeFragment")
                    .commit()
            }
        }
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

    override fun onViewLongPress(index: Int) {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(actionModeCallback)!!
            reminderList[index].selected = !reminderList[index].selected
            requireView().reminder_recycler.adapter?.notifyItemChanged(index)
        } else {
            return
        }
    }

    override fun onViewPress(index: Int) {
        if (actionMode == null) {
            return
        } else {
            reminderList[index].selected = !reminderList[index].selected
            requireView().reminder_recycler.adapter?.notifyItemChanged(index)
        }
    }
}