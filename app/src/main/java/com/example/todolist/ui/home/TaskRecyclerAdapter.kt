package com.example.todolist.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.TaskList


class TaskRecyclerAdapter(context: Context?, data: MutableList<TaskList>) :
    RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>() {
    private val mData: MutableList<TaskList> = data

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = mData[position].title
        holder.date.text = mData[position].date.toString()
        holder.task.text = mData[position].task
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val date: TextView
        val task: TextView

        init {
            title = itemView.findViewById(R.id.list_title)
            date = itemView.findViewById(R.id.list_date)
            task = itemView.findViewById(R.id.tasks)
        }
    }
}
