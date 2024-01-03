package com.lavender.reminder.data.reminder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lavender.reminder.util.Async
import com.lavender.reminder.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ReminderListState(
    val reminders: List<Reminder> = listOf(),
)

@HiltViewModel
class ReminderListModel @Inject constructor(
    repository: ReminderRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val tag = "ReminderListModel"

    private val _savedSortOrder = savedStateHandle.getStateFlow(
        "ReminderSortOrder", ReminderSortOrder.DAYS_REMAINING
    )

    private val _allRemindersAsync =
        combine(repository.getRemindersStream(), _savedSortOrder) { reminders, sortOrder ->
            sortReminders(reminders = reminders, sortOrder = sortOrder)
        }.map { reminders ->
            Log.d(tag, "_allRemindersAsync (reminders.size: ${reminders.size})")
            Async.Success(reminders)
        }

    val uiState: StateFlow<ReminderListState> =
        _allRemindersAsync.map { ReminderListState(reminders = it.data) }.stateIn(
            scope = viewModelScope, started = WhileUiSubscribed, initialValue = ReminderListState()
        )

    fun toggleSortOrder() {
        Log.d(tag, "toggleSortOrder")
        // TODO: do something better
        when (_savedSortOrder.value) {
            ReminderSortOrder.DAY -> setSortOrder(ReminderSortOrder.DAYS_REMAINING)
            ReminderSortOrder.DAYS_REMAINING -> setSortOrder(ReminderSortOrder.DAY)
        }
    }

    fun setSortOrder(sortOrder: ReminderSortOrder) {
        Log.d(tag, "setSortOrder (sortOrder: $sortOrder)")
        savedStateHandle["ReminderSortOrder"] = sortOrder
    }

    private fun sortReminders(
        reminders: List<Reminder>, sortOrder: ReminderSortOrder
    ): List<Reminder> {
        Log.d(tag, "sortReminders (sortOrder: $sortOrder)")
        return when (sortOrder) {
            ReminderSortOrder.DAY -> reminders.sortedBy { it.start.value }
            ReminderSortOrder.DAYS_REMAINING -> reminders.sortedBy {
                (7 * it.frequency) - (it.progress.first + (7 * it.progress.second))
            }
        }
    }

    enum class ReminderSortOrder {
        DAY, DAYS_REMAINING,
    }

}