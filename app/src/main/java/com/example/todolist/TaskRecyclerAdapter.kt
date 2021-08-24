package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskRecyclerAdapter(data: MutableList<Task>, listener: RecyclerCallback) :
    RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>() {

    private val mData: MutableList<Task> = data
    private val mListener: RecyclerCallback = listener

    interface RecyclerCallback {
        fun removeAtIndex(index: Int)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = mData[position].title
        holder.description.text = mData[position].description

        if (mData[position].isCompleted) {
            holder.foreground.alpha = 0.25f
        } else {
            holder.foreground.alpha = 1f
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        View.OnLongClickListener
    {
        val title: TextView
        val description: TextView
        val foreground: RelativeLayout
        val background: RelativeLayout

        init {
            title = itemView.findViewById(R.id.card_title)
            description = itemView.findViewById(R.id.card_details)
            foreground = itemView.findViewById(R.id.task_foreground)
            background = itemView.findViewById(R.id.task_background)

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition

            mData[position].isCompleted = !mData[position].isCompleted
            notifyItemChanged(position)
        }

        override fun onLongClick(v: View?): Boolean {
            val position: Int = adapterPosition

            mListener.removeAtIndex(position)
            notifyItemRemoved(position)

            return true
        }
    }
}