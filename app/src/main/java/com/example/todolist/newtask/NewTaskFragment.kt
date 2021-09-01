package com.example.todolist.newtask

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.R
import com.example.todolist.Task
import kotlinx.android.synthetic.main.fragment_new_task.view.*
import kotlin.math.roundToInt
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.N)
class NewTaskFragment : Fragment() {

    private lateinit var communicator: Communicator
    private val calendar = Calendar.getInstance()
    private val activeDays: ArrayList<Boolean> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_task, container, false)
        communicator = activity as Communicator

        val currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val checkboxes: ArrayList<CheckBox> = arrayListOf(
            view.checkbox_sun,
            view.checkbox_mon,
            view.checkbox_tue,
            view.checkbox_wed,
            view.checkbox_thu,
            view.checkbox_fri,
            view.checkbox_sat)

        checkboxes[currentDay].isChecked = true

        view.create_new_task.setOnClickListener {
            checkboxes.forEach { activeDays.add(it.isChecked) }

            communicator.sendTaskData(Task(
                Random.nextInt(0, 100),
                view.new_task_title.text.toString(),
                view.new_task_time.hour,
                view.new_task_time.minute,
                activeDays,
                view.checkbox_recurring.isChecked)
            )
        }
        return view
    }
}