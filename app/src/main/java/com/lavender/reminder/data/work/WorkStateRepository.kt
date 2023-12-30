package com.lavender.reminder.data.work

import android.util.Log
import javax.inject.Inject

class WorkStateRepository @Inject constructor(
    private val localDataSource: WorkStateDao
) {
    private val tag = "WorkStateRepository"

    suspend fun updateWorkState(updateTime: String) {
        val workState = WorkState(lastUpdateTime = updateTime)

        Log.d(tag, "updateWorkState (workState: $workState")
        localDataSource.upsert(workState.toLocal())
    }

    suspend fun getWorkState(): WorkState {
        return localDataSource.get()?.toExternal() ?: WorkState()
    }
}