package com.lavender.reminder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lavender.reminder.screens.ReminderDetailsScreen
import com.lavender.reminder.screens.ReminderListScreen
import com.lavender.reminder.ui.theme.ReminderTheme
import com.lavender.reminder.work.UpdateProgressWork
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleWork()

        setContent {
            ReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ReminderNavHost(updateProgress = this::scheduleWork)
                }
            }
        }
    }

    private fun scheduleWork() {
        val workRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<UpdateProgressWork>(
            repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()

        Log.d(tag, "scheduleWork (workRequest: $workRequest)")
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "UpdateProgress", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest
        )
    }
}

@Composable
fun ReminderNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main",
    updateProgress: () -> Unit
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
        composable(route = "main") {
            ReminderListScreen(onNavigateToDetails = { uuid: String ->
                navController.navigate(
                    route = "details/${uuid}"
                )
            })
        }

        composable("details/{uuid}") {
            ReminderDetailsScreen(navigateToMain = {
                navController.navigate(
                    route = "main"
                )
            }, updateProgress = updateProgress)
        }
    }
}