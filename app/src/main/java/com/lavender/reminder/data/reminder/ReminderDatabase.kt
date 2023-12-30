package com.lavender.reminder.data.reminder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalReminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao
}