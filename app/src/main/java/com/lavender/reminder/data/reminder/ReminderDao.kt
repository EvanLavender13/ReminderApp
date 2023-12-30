package com.lavender.reminder.data.reminder

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder")
    fun observeAll(): Flow<List<LocalReminder>>

    @Query("SELECT * FROM reminder WHERE uuid = :uuid")
    fun observeById(uuid: String): Flow<LocalReminder>

    @Query("SELECT * FROM reminder")
    suspend fun getAll(): List<LocalReminder>

    @Query("SELECT * FROM reminder WHERE uuid = :uuid")
    suspend fun getById(uuid: String): LocalReminder?

    @Upsert
    suspend fun upsert(reminder: LocalReminder)

    @Query("DELETE FROM reminder WHERE uuid = :uuid")
    suspend fun deleteById(uuid: String): Int
}