package com.lavender.reminder.data.work

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work")
data class LocalWorkState(
    @PrimaryKey val id: Int = 1,
    val lastUpdateTime: String
)

fun LocalWorkState.toExternal() = WorkState(
    lastUpdateTime = lastUpdateTime
)