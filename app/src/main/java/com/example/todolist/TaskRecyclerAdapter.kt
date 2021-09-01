package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R.drawable.*

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

        val dayArray: ArrayList<String> = arrayListOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        var daysString = ""
        mData[position].days.indices.forEach { i ->
            if (mData[position].days[i]) { daysString += "${dayArray[i]} " }
        }

        holder.title.text = mData[position].title
        holder.time.text = "${mData[position].hour}:${mData[position].minute}"
        holder.toggle.isChecked = mData[position].isActive
        holder.days.text = daysString
        when {
            mData[position].recurring -> {
                holder.recurring.setBackgroundResource(ic_baseline_repeat_24)
            }
            else -> {
                holder.recurring.setBackgroundResource(ic_outline_looks_one_24)
            }
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener
    {
        val title: TextView = itemView.findViewById(R.id.card_title)
        val time: TextView = itemView.findViewById(R.id.card_time)
        val toggle: Switch = itemView.findViewById(R.id.card_toggle)
        val days: TextView = itemView.findViewById(R.id.card_days)
        val recurring: ImageView = itemView.findViewById(R.id.card_status)

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val position: Int = adapterPosition

            mListener.removeAtIndex(position)
            notifyItemRemoved(position)

            return true
        }
    }
}