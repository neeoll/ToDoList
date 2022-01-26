package com.example.todolist

import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R.drawable.*
import com.google.android.material.switchmaterial.SwitchMaterial

class RecyclerAdapter(data: MutableList<Reminder>, listener: RecyclerCallback) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var mData: MutableList<Reminder> = data
    private val mListener: RecyclerCallback = listener

    interface RecyclerCallback {
        fun removeAtIndex(index: Int)
        fun switchAlarm(data: Reminder, isChecked: Boolean)
        fun onViewLongPress(index: Int)
        fun onViewPress(index: Int)
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

        val timeString = "${mData[position].hour}:${mData[position].minute}"

        holder.title.text = mData[position].title
        holder.time.text = timeString
        holder.days.text = daysString
        holder.toggle.isChecked = mData[position].isActive
        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            mListener.switchAlarm(mData[position], !isChecked)
        }
        when (mData[position].recurring) {
            true -> holder.recurring.setBackgroundResource(reminder_repeat)
            false -> holder.recurring.setBackgroundResource(reminder_once)
        }

        when (mData[position].selected) {
            true -> holder.foreground.visibility = View.VISIBLE
            false ->holder.foreground.visibility = View.INVISIBLE
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
        val toggle: SwitchMaterial = itemView.findViewById(R.id.card_toggle)
        val days: TextView = itemView.findViewById(R.id.card_days)
        val foreground: RelativeLayout = itemView.findViewById(R.id.reminder_foreground)
        val recurring: ImageView = itemView.findViewById(R.id.card_status)

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            mListener.onViewLongPress(adapterPosition)
            return true
        }

        override fun onClick(v: View?) {
            mListener.onViewPress(adapterPosition)
        }
    }
}