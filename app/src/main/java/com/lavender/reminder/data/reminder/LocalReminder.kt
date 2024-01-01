package com.lavender.reminder.data.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "reminder")
data class LocalReminder(
    @PrimaryKey var uuid: String,
    var name: String,
    var start: DayOfWeek,
    var frequency: Int,
    var progressDays: Int,
    var progressWeeks: Int
)

fun LocalReminder.toExternal() = Reminder(
    uuid = uuid,
    name = name,
    start = start,
    frequency = frequency,
    progress = Pair(progressDays, progressWeeks)
)

fun List<LocalReminder>.toExternal() = map(LocalReminder::toExternal)
