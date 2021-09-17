package com.example.todolist

interface Communicator {
    fun receiveReminderData(data: Reminder, action: String)
}