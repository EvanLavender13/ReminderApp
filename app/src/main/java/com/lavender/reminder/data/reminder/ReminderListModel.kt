package com.lavender.reminder.data.reminder

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
class ReminderListModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _allRemindersAsync = repository.getRemindersStream().map { Async.Success(it) }

    val uiState: StateFlow<ReminderListState> =
        _allRemindersAsync.map { ReminderListState(reminders = it.data) }.stateIn(
            scope = viewModelScope, started = WhileUiSubscribed, initialValue = ReminderListState()
        )

}