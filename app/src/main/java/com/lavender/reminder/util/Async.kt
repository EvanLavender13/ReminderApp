package com.lavender.reminder.util

import kotlinx.coroutines.flow.SharingStarted

sealed class Async<out T> {
    data object Loading : Async<Nothing>()

    data class Error(val errorMessage: Int) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}

private const val StopTimeoutMillis: Long = 5000

val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)