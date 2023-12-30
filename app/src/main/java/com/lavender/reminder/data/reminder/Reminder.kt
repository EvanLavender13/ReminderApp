package com.lavender.reminder.data.reminder

import java.time.DayOfWeek

data class Reminder(
    val uuid: String,
    val name: String,
    val start: DayOfWeek,
    val frequency: Int,
    val progress: Int
)

fun Reminder.toLocal() = LocalReminder(
    uuid = uuid,
    name = name,
    start = start,
    frequency = frequency,
    progress = progress
)

fun List<Reminder>.toLocal() = map(Reminder::toLocal)