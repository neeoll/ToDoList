package com.example.todolist.newreminder

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.R
import com.example.todolist.Reminder
import kotlinx.android.synthetic.main.fragment_new_reminder.view.*
import kotlin.random.Random

class NewReminderFragment : Fragment() {

    private lateinit var communicator: Communicator
    private val activeDays: ArrayList<Boolean> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_reminder, container, false)
        communicator = activity as Communicator

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        val checkboxes: ArrayList<CheckBox> = arrayListOf(
            view.checkbox_sun,
            view.checkbox_mon,
            view.checkbox_tue,
            view.checkbox_wed,
            view.checkbox_thu,
            view.checkbox_fri,
            view.checkbox_sat)

        checkboxes[currentDay].isChecked = true

        view.create_reminder.setOnClickListener {
            checkboxes.forEach { activeDays.add(it.isChecked) }

            communicator.receiveReminderData(Reminder(
                Random.nextInt(0, 100),
                view.new_reminder_title.text.toString(),
                view.new_reminder_time.hour,
                view.new_reminder_time.minute,
                activeDays,
                view.recurring_toggle.isChecked),
                "create"
            )
        }

        return view
    }
}