package com.lavender.reminder.data.reminder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lavender.reminder.util.Async
import com.lavender.reminder.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ReminderListState(
    val reminders: List<Reminder> = listOf(),
)

@HiltViewModel
class ReminderListModel @Inject constructor(repository: ReminderRepository) : ViewModel() {
    private val tag = "ReminderListModel"

    private val _allRemindersAsync = repository.getRemindersStream().map { reminders ->
        Log.d(tag, "_allRemindersAsync (reminders.size: ${reminders.size})")
        Async.Success(reminders.sortedBy { -(it.progress.first + (7 * it.progress.second)) })
    }

    val uiState: StateFlow<ReminderListState> =
        _allRemindersAsync.map { ReminderListState(reminders = it.data) }.stateIn(
            scope = viewModelScope, started = WhileUiSubscribed, initialValue = ReminderListState()
        )

}