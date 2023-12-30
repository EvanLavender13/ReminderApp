package com.lavender.reminder.data.work

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface WorkStateDao {

    @Upsert
    suspend fun upsert(workState: LocalWorkState)

    @Query("SELECT * FROM work WHERE id = 1")
    suspend fun get(): LocalWorkState?
}