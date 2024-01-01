package com.lavender.reminder.data.reminder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

data class ReminderDetailsState(
    val new: Boolean = false,
    val name: String = "Reminder",
    val start: DayOfWeek = DayOfWeek.MONDAY,
    val frequency: Int = 1,
    val progress: Pair<Int, Int> = Pair(0, 0)
)

@HiltViewModel
class ReminderDetailsModel @Inject constructor(
    private val repository: ReminderRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val tag = "ReminderDetailsModel"

    private val _uiState = MutableStateFlow(ReminderDetailsState())
    val uiState = _uiState.asStateFlow()

    val uuid: String? = savedStateHandle["uuid"]

    init {
        if (uuid != null) {
            loadReminder(uuid)
        }
    }

    fun saveReminder() {
        Log.d(tag, "saveReminder (uuid: $uuid)")
        if (uuid == "0") {
            createNewReminder()
        } else {
            updateReminder()
        }
    }

    fun deleteReminder() {
        Log.d(tag, "deleteReminder (uuid: $uuid)")
        viewModelScope.launch {
            if (uuid != null && uuid != "0") {
                repository.deleteReminder(uuid)
            }
        }
    }

    fun updateName(name: String) {
        Log.d(tag, "updateName (name: $name uuid: $uuid)")
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateStart(start: DayOfWeek) {
        Log.d(tag, "updateStart (start: $start uuid: $uuid)")
        _uiState.update {
            it.copy(start = start)
        }
    }

    fun updateFrequency(frequency: Int) {
        Log.d(tag, "updateFrequency (frequency: $frequency uuid: $uuid)")
        _uiState.update {
            it.copy(frequency = frequency)
        }
    }

    private fun createNewReminder() {
        Log.d(tag, "createNewReminder (uuid: $uuid)")
        viewModelScope.launch {
            repository.createReminder(
                name = uiState.value.name,
                start = uiState.value.start,
                frequency = uiState.value.frequency
            )
        }
    }

    private fun updateReminder() {
        Log.d(tag, "updateReminder (uuid: $uuid)")
        viewModelScope.launch {
            uuid?.let {
                repository.updateReminder(
                    uuid = it,
                    name = uiState.value.name,
                    start = uiState.value.start,
                    frequency = uiState.value.frequency
                )
            }
        }
    }

    private fun loadReminder(uuid: String) {
        Log.d(tag, "loadReminder (uuid: $uuid)")
        viewModelScope.launch {
            repository.getReminder(uuid)?.let { reminder ->
                Log.d(tag, "loadReminder (reminder: $reminder)")
                _uiState.update {
                    it.copy(
                        new = false,
                        name = reminder.name,
                        start = reminder.start,
                        frequency = reminder.frequency,
                        progress = reminder.progress
                    )
                }
            } ?: _uiState.update {
                it.copy(new = true)
            }
        }
    }
}