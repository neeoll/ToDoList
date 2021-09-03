package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R.drawable.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class RecyclerAdapter(data: MutableList<Reminder>, listener: RecyclerCallback, view: View) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val mData: MutableList<Reminder> = data
    private val mListener: RecyclerCallback = listener
    private val mView: View = view

    interface RecyclerCallback {
        fun removeAtIndex(index: Int)
        fun switchAlarm(data: Reminder, isChecked: Boolean)
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

            /*val temp = mData.removeAt(position)
            notifyItemRemoved(position)

            val layout: RecyclerView = mView.findViewById(R.id.reminder_recycler)
            Snackbar.make(layout, "Reminder Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    mData.add(position, temp)
                    notifyItemInserted(position)
                }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            mListener.removeAtIndex(position)
                        }
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }
            }).show()*/

            return true
        }
    }
}