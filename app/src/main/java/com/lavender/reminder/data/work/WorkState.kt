package com.lavender.reminder.data.work

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

data class WorkState(
    val lastUpdateTime: String = Instant.now().toString()
)

fun WorkState.toLocal() = LocalWorkState(
    lastUpdateTime = lastUpdateTime
)
