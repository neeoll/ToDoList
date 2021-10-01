package com.example.todolist.editreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.R
import com.example.todolist.Reminder
import kotlinx.android.synthetic.main.fragment_edit_reminder.view.*

class EditReminderFragment(data: Reminder): Fragment() {

    private val reminderToEdit = data
    private lateinit var communicator: Communicator
    private val activeDays: ArrayList<Boolean> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_reminder, container, false)
        communicator = activity as Communicator

        val checkboxes: ArrayList<CheckBox> = arrayListOf(
            view.checkbox_sun,
            view.checkbox_mon,
            view.checkbox_tue,
            view.checkbox_wed,
            view.checkbox_thu,
            view.checkbox_fri,
            view.checkbox_sat)

        view.update_reminder_title.setText(reminderToEdit.title)
        view.update_reminder_time.hour = reminderToEdit.hour
        view.update_reminder_time.minute = reminderToEdit.minute

        for (i in checkboxes.indices) {
            if (reminderToEdit.days[i]) {
                checkboxes[i].isChecked = true
            }
        }

        view.recurring_toggle.isChecked = reminderToEdit.recurring

        view.update_reminder.setOnClickListener {
            checkboxes.forEach { activeDays.add(it.isChecked) }

            communicator.receiveReminderData(Reminder(
                reminderToEdit.id,
                view.update_reminder_title.text.toString(),
                view.update_reminder_time.hour,
                view.update_reminder_time.minute,
                activeDays,
                view.recurring_toggle.isChecked),
                "update"
            )
        }

        return view
    }
}