package com.example.todolist.newreminder

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.Reminder
import com.example.todolist.databinding.FragmentNewReminderBinding
import kotlin.random.Random

class NewReminderFragment : Fragment() {

    private lateinit var binding: FragmentNewReminderBinding
    private lateinit var communicator: Communicator
    private val activeDays: ArrayList<Boolean> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewReminderBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        val checkboxes: ArrayList<CheckBox> = arrayListOf(
            binding.checkboxSun,
            binding.checkboxMon,
            binding.checkboxTue,
            binding.checkboxWed,
            binding.checkboxThu,
            binding.checkboxFri,
            binding.checkboxSat)

        checkboxes[currentDay].isChecked = true

        binding.createReminder.setOnClickListener {
            checkboxes.forEach { activeDays.add(it.isChecked) }

            communicator.receiveReminderData(Reminder(
                Random.nextInt(0, 100),
                binding.newReminderTitle.text.toString(),
                binding.newReminderTime.hour,
                binding.newReminderTime.minute,
                activeDays,
                binding.recurringToggle.isChecked),
                "create"
            )
        }

        return binding.root
    }
}