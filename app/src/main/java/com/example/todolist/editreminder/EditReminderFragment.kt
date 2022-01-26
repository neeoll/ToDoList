package com.example.todolist.editreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.Reminder
import com.example.todolist.databinding.FragmentEditReminderBinding

class EditReminderFragment(data: Reminder): Fragment() {

    private lateinit var binding: FragmentEditReminderBinding
    private lateinit var communicator: Communicator
    private val reminderToEdit = data
    private val activeDays: ArrayList<Boolean> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditReminderBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        val checkboxes: ArrayList<CheckBox> = arrayListOf(
            binding.checkboxSun,
            binding.checkboxMon,
            binding.checkboxTue,
            binding.checkboxWed,
            binding.checkboxThu,
            binding.checkboxFri,
            binding.checkboxSat)

        binding.updateReminderTitle.setText(reminderToEdit.title)
        binding.updateReminderTime.hour = reminderToEdit.hour
        binding.updateReminderTime.minute = reminderToEdit.minute

        for (i in checkboxes.indices) {
            if (reminderToEdit.days[i]) {
                checkboxes[i].isChecked = true
            }
        }

        binding.recurringToggle.isChecked = reminderToEdit.recurring

        binding.updateReminder.setOnClickListener {
            checkboxes.forEach { activeDays.add(it.isChecked) }

            communicator.receiveReminderData(Reminder(
                reminderToEdit.alarmId,
                binding.updateReminderTitle.text.toString(),
                binding.updateReminderTime.hour,
                binding.updateReminderTime.minute,
                activeDays,
                binding.recurringToggle.isChecked),
                "update"
            )
        }

        return binding.root
    }
}