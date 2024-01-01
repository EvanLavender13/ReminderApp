package com.lavender.reminder.data.reminder

import android.util.Log
import com.lavender.reminder.inject.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val localDataSource: ReminderDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val tag = "ReminderRepository"

    suspend fun createReminder(name: String, start: DayOfWeek, frequency: Int) {
        val uuid = UUID.randomUUID().toString()
        val reminder = Reminder(
            uuid = uuid, name = name, start = start, frequency = frequency, progress = Pair(0, 0)
        )

        Log.d(tag, "createReminder (reminder: $reminder)")
        localDataSource.upsert(reminder.toLocal())
    }

    suspend fun updateReminder(uuid: String, name: String, start: DayOfWeek, frequency: Int) {
        val reminder = getReminder(uuid)?.copy(name = name, start = start, frequency = frequency)
            ?: throw Exception("Reminder (uuid $uuid) not found")

        Log.d(tag, "updateReminder (reminder: $reminder)")
        localDataSource.upsert(reminder.toLocal())
    }

    suspend fun updateProgress(uuid: String, progress: Pair<Int, Int>) {
        val reminder = getReminder(uuid)?.copy(progress = progress)
            ?: throw Exception("Reminder (uuid $uuid) not found")

        Log.d(tag, "updateProgress (reminder: $reminder)")
        localDataSource.upsert(reminder.toLocal())
    }

    suspend fun deleteReminder(uuid: String): Int {
        Log.d(tag, "deleteReminder (uuid: $uuid)")
        return localDataSource.deleteById(uuid)
    }

    fun getRemindersStream(): Flow<List<Reminder>> {
        return localDataSource.observeAll().map {
            withContext(dispatcher) {
                it.toExternal()
            }
        }
    }

    suspend fun getReminders(): List<Reminder> {
        return localDataSource.getAll().toExternal()
    }

    fun getReminderStream(uuid: String): Flow<Reminder?> {
        return localDataSource.observeById(uuid).map { it.toExternal() }
    }

    suspend fun getReminder(uuid: String): Reminder? {
        return localDataSource.getById(uuid)?.toExternal()
    }
}