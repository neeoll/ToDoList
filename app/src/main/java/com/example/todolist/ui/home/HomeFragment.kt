package com.example.todolist.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.TaskList
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {

    private val mainActivity: MainActivity = MainActivity()
    private var adapter: RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>? = null
    private var taskLists = ArrayList<TaskList>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        Log.e("VIEW", "Fragment onViewCreated")
        if (MainActivity().mData != null) {
            Log.e("VIEW", mainActivity.mData.toString())
            insertTask(0, mainActivity.mData)
        }

        task_lists_recycler.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = TaskRecyclerAdapter(this.context, taskLists)
        }

        add_list.setOnClickListener {
            insertTask(0, TaskList("TEST", LocalDate.now(), "TEST"))
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_newTaskFragment)
        }
    }

    fun insertTask(index: Int, taskList: TaskList) {
        taskLists.add(index, taskList)
        adapter?.notifyItemInserted(index)
    }
}
