package com.lavender.reminder.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lavender.reminder.data.reminder.Reminder
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
    private val tag = "UpdateProgressWork"

    private lateinit var reminderRepository: ReminderRepository
    private lateinit var workStateRepository: WorkStateRepository

    init {
        injectFields()
    }

    override suspend fun doWork(): Result {
        val workState = workStateRepository.getWorkState()
        Log.d(tag, "doWork (workState: $workState)")

        val updateTime = Instant.now()
        Log.d(tag, "doWork (updateTime: $updateTime)")

        val lastUpdateTime = Instant.parse(workState.lastUpdateTime)
        Log.d(tag, "doWork (lastUpdateTime: $lastUpdateTime)")

        val timeSinceLastUpdate = updateTime.epochSecond - lastUpdateTime.epochSecond
        Log.d(tag, "doWork (timeSinceLastUpdate: ${timeSinceLastUpdate}s)")

        val updateDay = updateTime.atZone(ZoneId.systemDefault()).dayOfWeek
        val lastUpdateDay = lastUpdateTime.atZone(ZoneId.systemDefault()).dayOfWeek
        Log.d(tag, "doWork (updateDay: $updateDay lastUpdateDay: $lastUpdateDay)")

        val completed: MutableList<Reminder> = mutableListOf()

        reminderRepository.getReminders().forEach {
            Log.d(tag, "doWork (begin: ${it.uuid})")
            Log.d(tag, "doWork (reminder: $it)")

            // TODO: this whole "progress" thing needs a rework
            val dayChanged = updateDay != lastUpdateDay

            val currentProgress = it.progress
            Log.d(tag, "doWork (currentProgress: $currentProgress)")

            val progressDays = currentProgress.first
            val progressWeeks = currentProgress.second

            var newProgressDays = progressDays
            var newProgressWeeks = progressWeeks

            if (dayChanged && newProgressDays == 7) {
                if (newProgressWeeks + 1 == it.frequency) {
                    Log.d(tag, "doWork (reset)")
                    newProgressDays = 1
                    newProgressWeeks = 0
                } else {
                    Log.d(tag, "doWork (increment week)")
                    newProgressDays = 1
                    newProgressWeeks++
                }
            } else {
                val dayDifference = updateDay.value - it.start.value
                newProgressDays = if (dayDifference > 0) dayDifference else 7 + dayDifference
                Log.d(tag, "doWork (newProgressDays: $newProgressDays)")
            }

            if (newProgressDays == 7 && newProgressWeeks + 1 == it.frequency) {
                Log.d(tag, "doWork (complete)")
                completed.add(it)
            }

            val newProgress = Pair(newProgressDays, newProgressWeeks)
            if (newProgress != currentProgress) {
                Log.d(tag, "doWork (newProgress: $newProgress)")
                reminderRepository.updateProgress(it.uuid, Pair(newProgressDays, newProgressWeeks))
            } else {
                Log.d(tag, "doWork (no updates)")
            }

            Log.d(tag, "doWork (end: ${it.uuid})")
        }

        Log.d(tag, "doWork (completed.size: ${completed.size})")

        val lastNotificationTime = Instant.parse(workState.lastNotificationTime)
        Log.d(tag, "doWork (lastNotificationTime: $lastNotificationTime)")

        val timeSinceLastNotification = updateTime.epochSecond - lastNotificationTime.epochSecond
        Log.d(tag, "doWork (timeSinceLastNotification: ${timeSinceLastNotification}s)")

        var notificationTime = lastNotificationTime

        if (completed.isNotEmpty() && (timeSinceLastNotification >= 43200)) {
            Log.d(tag, "doWork (building notification)")
            val stringBuilder = StringBuilder()

            completed.forEach {
                stringBuilder.append("${it.start.name.lowercase()}: ${it.name}\n")
            }

            val notificationContent = stringBuilder.toString().dropLast(1)
            Log.d(tag, "doWork (notificationContent: $notificationContent)")

            with(NotificationManagerCompat.from(applicationContext)) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(tag, "Notification permission not granted")
                    return@with
                }

                if (areNotificationsEnabled()) {
                    val channelId = "ReminderAppChannel"
                    val notificationBuilder =
                        NotificationCompat.Builder(applicationContext, channelId)
                            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
                            .setContentTitle("Hey buddy").setContentText(stringBuilder.toString())
                            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true)

                    Log.i(tag, "doWork (sending notification)")
                    notify(1, notificationBuilder.build())

                    notificationTime = updateTime
                }
            }
        }

        workStateRepository.updateWorkState(updateTime.toString(), notificationTime.toString())

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