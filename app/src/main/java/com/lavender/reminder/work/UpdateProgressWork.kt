package com.lavender.reminder.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lavender.reminder.data.reminder.ReminderRepository
import com.lavender.reminder.data.work.WorkStateRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import java.time.ZoneId

class UpdateProgressWork(
    appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val tag = "TestWork"

    private lateinit var reminderRepository: ReminderRepository
    private lateinit var workStateRepository: WorkStateRepository

    init {
        injectFields()
    }

    override suspend fun doWork(): Result {
        val workState = workStateRepository.getWorkState()
        Log.d(tag, "doWork (workState: $workState)")

        val lastUpdateTime = Instant.parse(workState.lastUpdateTime)
        Log.d(tag, "doWork (lastUpdateTime: $lastUpdateTime)")

        val updateTime = Instant.now()
        val timeSinceLastUpdate = updateTime.epochSecond - lastUpdateTime.epochSecond
        Log.d(tag, "doWork (timeSinceLastUpdate: ${timeSinceLastUpdate}s)")

        val updateDay = updateTime.atZone(ZoneId.systemDefault()).dayOfWeek
        val lastUpdateDay = lastUpdateTime.atZone(ZoneId.systemDefault()).dayOfWeek
        Log.d(tag, "doWork (updateDay: $updateDay lastUpdateDay: $lastUpdateDay)")

        reminderRepository.getReminders().forEach {
            val difference = updateDay.value - it.start.value
            val progress = if (difference > 0) difference else 7 + difference
            Log.d(tag, "doWork (progress: $progress)")

            reminderRepository.updateProgress(it.uuid, progress)
        }

        workStateRepository.updateWorkState(updateTime.toString())

        return Result.success()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReminderRepositoryEntryPoint {
        fun reminderRepository(): ReminderRepository
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkStateRepositoryEntryPoint : ReminderRepositoryEntryPoint {
        fun workStateRepository(): WorkStateRepository
    }

    private fun injectFields() {
        var entryPoint = EntryPointAccessors.fromApplication(
            applicationContext, ReminderRepositoryEntryPoint::class.java
        )
        reminderRepository = entryPoint.reminderRepository()

        entryPoint = EntryPointAccessors.fromApplication(
            applicationContext, WorkStateRepositoryEntryPoint::class.java
        )
        workStateRepository = entryPoint.workStateRepository()
    }
}