package com.example.todolist

import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R.drawable.*

class RecyclerAdapter(data: MutableList<Reminder>, listener: RecyclerCallback) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var mData: MutableList<Reminder> = data
    private val mListener: RecyclerCallback = listener
    private var multiSelect: Boolean = false

    interface RecyclerCallback {
        fun removeAtIndex(index: Int)
        fun switchAlarm(data: Reminder, isChecked: Boolean)
        fun openEditFragment(data: Reminder)
        fun onViewLongPress(index: Int)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_card, parent, false)
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
        holder.days.text = daysString
        holder.toggle.isChecked = mData[position].isActive
        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            mListener.switchAlarm(mData[position], isChecked)
        }
        when {
            mData[position].recurring -> {
                holder.recurring.setBackgroundResource(reminder_repeat)
            }
            else -> {
                holder.recurring.setBackgroundResource(reminder_once)
            }
        }

        if (mData[position].selected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFF00"))
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener
    {
        val title: TextView = itemView.findViewById(R.id.card_title)
        val time: TextView = itemView.findViewById(R.id.card_time)
        val toggle: Switch = itemView.findViewById(R.id.card_toggle)
        val days: TextView = itemView.findViewById(R.id.card_days)
        val recurring: ImageView = itemView.findViewById(R.id.card_status)
        var isSelected = false

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            mListener.onViewLongPress(adapterPosition)
            return true
        }

        override fun onClick(v: View?) {
            if (!multiSelect) { return }
            else {
                isSelected = !isSelected
                notifyItemChanged(adapterPosition)
            }
        }
    }
}