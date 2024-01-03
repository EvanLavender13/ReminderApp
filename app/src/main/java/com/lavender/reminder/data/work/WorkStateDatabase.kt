package com.lavender.reminder.data.work

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalWorkState::class], version = 2, exportSchema = false)
abstract class WorkStateDatabase : RoomDatabase() {

    abstract fun workStateDao(): WorkStateDao
}