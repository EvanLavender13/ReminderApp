package com.lavender.reminder.data.work

import java.time.Instant

data class WorkState(
    // just set it to "yesterday"
    val lastUpdateTime: String = Instant.now().toString(),
    val lastNotificationTime: String = Instant.now().toString()
)

fun WorkState.toLocal() = LocalWorkState(
    lastUpdateTime = lastUpdateTime, lastNotificationTime = lastNotificationTime
)
