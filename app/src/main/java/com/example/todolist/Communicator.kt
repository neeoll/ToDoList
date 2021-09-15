package com.example.todolist

interface Communicator {
    fun createReminderData(data: Reminder)
    fun updateReminderData(data: Reminder)
}