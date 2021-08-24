package com.example.todolist.newtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todolist.Communicator
import com.example.todolist.R
import com.example.todolist.Task
import kotlinx.android.synthetic.main.fragment_new_task.view.*

class NewTaskFragment : Fragment() {

    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_task, container, false)
        communicator = activity as Communicator

        view.create_new_task.setOnClickListener {
            communicator.sendTaskData(Task(
                view.new_task_title.text.toString(),
                view.new_task_date.text.toString(),
                view.new_task_description.text.toString()
                )
            )
        }
        return view
    }
}