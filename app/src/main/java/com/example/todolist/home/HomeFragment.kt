package com.example.todolist.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.SwipeToDelete
import com.example.todolist.Task
import com.example.todolist.TaskRecyclerAdapter
import com.example.todolist.newtask.NewTaskFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.ObjectOutputStream

class HomeFragment(listener: HomeCallback) : Fragment(),
    TaskRecyclerAdapter.RecyclerCallback {

    private var taskList: ArrayList<Task> = arrayListOf()
    private var communicatorData: ArrayList<Task> = arrayListOf()
    private var recyclerAdapter: RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>? = null
    private val homeListener: HomeCallback = listener
    private val filename: String = "taskList"

    interface HomeCallback {
        fun removeAtIndex(index: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        if (arguments != null) {
            communicatorData = arguments?.getSerializable("data") as ArrayList<Task>
            for (item in communicatorData) {
                insertTask(0, Task(
                    item.title, item.date, item.description, item.isCompleted)
                )
            }
        }

        val swipeToDelete = object: SwipeToDelete(requireContext(), 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeAtIndex(viewHolder.adapterPosition)
                recyclerAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }

        val helper = ItemTouchHelper(swipeToDelete)

        view.task_lists_recycler.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = TaskRecyclerAdapter(taskList, this@HomeFragment)
            helper.attachToRecyclerView(this)
        }

        view.add_list.setOnClickListener {
            val newTaskFragment = NewTaskFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager
            val transaction = supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, newTaskFragment).commit()
        }

        return view
    }

    private fun insertTask(index: Int, task: Task) {
        this.taskList.add(index, task)
        recyclerAdapter?.notifyItemInserted(index)
    }

    override fun onStop() {
        super.onStop()
        val fileOutputStream = context?.openFileOutput(filename,
            AppCompatActivity.MODE_PRIVATE
        )
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(taskList)
        fileOutputStream?.close()

        Log.e("TASKS", "contents written to file")
    }

    override fun removeAtIndex(index: Int) {
        taskList.removeAt(index)
        homeListener.removeAtIndex(index)
    }
}