package com.example.todolist.newtask

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.R
import com.example.todolist.Task
import kotlinx.android.synthetic.main.fragment_new_task.view.*
import kotlin.math.roundToInt


class NewTaskFragment : Fragment() {

    private lateinit var communicator: Communicator

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_task, container, false)
        communicator = activity as Communicator

        view.create_new_task.setOnClickListener {
            val activeDays: ArrayList<Boolean> = arrayListOf(
                view.checkbox_sun.isChecked,
                view.checkbox_mon.isChecked,
                view.checkbox_tue.isChecked,
                view.checkbox_wed.isChecked,
                view.checkbox_thu.isChecked,
                view.checkbox_fri.isChecked,
                view.checkbox_sat.isChecked)

            Log.e("HOME", activeDays.toString())

            communicator.sendTaskData(Task(
                Math.random().roundToInt(),
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