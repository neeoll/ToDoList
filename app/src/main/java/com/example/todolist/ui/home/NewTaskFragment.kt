package com.example.todolist.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.TaskList
import kotlinx.android.synthetic.main.fragment_new_task.*
import java.time.LocalDate

class NewTaskFragment : Fragment() {

    companion object {
        fun newInstance() = NewTaskFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_task, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        create_new_task.setOnClickListener {
            MainActivity().sendData(TaskList(
                new_task_title.text.toString(),
                LocalDate.now(),
                new_task_task.text.toString()
            ))

            NavHostFragment.findNavController(this).navigate(R.id.action_newTaskFragment_to_navigation_home)
        }
    }
}