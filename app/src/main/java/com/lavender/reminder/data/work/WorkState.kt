package com.lavender.reminder.data.work

import java.time.Instant

data class WorkState(
    val lastUpdateTime: String = Instant.now().toString()
)

fun WorkState.toLocal() = LocalWorkState(
    lastUpdateTime = lastUpdateTime
)
