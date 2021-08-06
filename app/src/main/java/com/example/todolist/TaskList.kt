package com.example.todolist

import java.time.LocalDate

data class TaskList(
    var title: String,
    var date: LocalDate,
    var task: String)