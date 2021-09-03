package com.example.todolist

import java.io.Serializable

class Reminder(
    val id: Int,
    val title: String,
    val hour: Int,
    val minute: Int,
    val days: ArrayList<Boolean>,
    val recurring: Boolean,
    var isActive: Boolean = true
): Serializable