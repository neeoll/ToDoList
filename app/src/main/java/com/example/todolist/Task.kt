package com.example.todolist

import java.io.Serializable

class Task(val title: String, val date: String, val description: String, var isCompleted: Boolean = false): Serializable