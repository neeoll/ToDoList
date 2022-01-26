package com.example.todolist

import java.io.Serializable

class Reminder(
    val alarmId: Int = 0,
    val title: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val days: ArrayList<Boolean> = arrayListOf(),
    val recurring: Boolean = false,
    var isActive: Boolean = true,
    var selected: Boolean = false,
    var pushId: String = ""
): Serializable