package com.lavender.reminder.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lavender.reminder.data.reminder.ReminderDetailsModel
import com.lavender.reminder.ui.theme.ReminderTheme
import java.time.DayOfWeek

@Composable
fun ReminderDetailsScreen(
    viewModel: ReminderDetailsModel = hiltViewModel(),
    navigateBack: () -> Unit,
    updateProgress: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(floatingActionButton = {
        Column(
            modifier = Modifier.padding(25.dp), verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (!uiState.new) {
                FloatingActionButton(onClick = {
                    viewModel.deleteReminder()
                    navigateBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete/Cancel",
                        tint = Color.Red
                    )
                }
            }

            FloatingActionButton(onClick = {
                navigateBack()
            }) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") }

            FloatingActionButton(onClick = {
                viewModel.saveReminder()
                updateProgress()
                navigateBack()
            }) { Icon(imageVector = Icons.Default.Done, contentDescription = "Save") }
        }

    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ReminderDetails(
                name = uiState.name,
                start = uiState.start,
                frequency = uiState.frequency,
                progress = uiState.progress,
                onNameChanged = viewModel::updateName,
                onStartChanged = viewModel::updateStart,
                onFrequencyChanged = viewModel::updateFrequency
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetails(
    name: String,
    start: DayOfWeek,
    frequency: Int,
    progress: Pair<Int, Int>,
    onNameChanged: (String) -> Unit,
    onStartChanged: (DayOfWeek) -> Unit,
    onFrequencyChanged: (Int) -> Unit
) {
    var startExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }

    val days = progress.first
    val weeks = progress.second

    Row {
        TextField(
            value = name,
            label = { Text(text = "Name") },
            modifier = Modifier.weight(1.0f),
            onValueChange = onNameChanged
        )
    }

    Row {
        ExposedDropdownMenuBox(
            expanded = startExpanded,
            onExpandedChange = { startExpanded = !startExpanded }) {
            TextField(value = start.name.lowercase(),
                label = { Text(text = "Start") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .weight(weight = 0.5f),
                onValueChange = { },
                readOnly = true
            )

            ExposedDropdownMenu(
                expanded = startExpanded,
                onDismissRequest = { startExpanded = false }) {
                DayOfWeek.values().forEach {
                    DropdownMenuItem(text = { Text(it.name.lowercase()) }, onClick = {
                        startExpanded = false
                        onStartChanged(it)
                    })
                }
            }
        }

        ExposedDropdownMenuBox(expanded = frequencyExpanded,
            onExpandedChange = { frequencyExpanded = !frequencyExpanded }) {
            TextField(value = "${frequency}w",
                label = { Text(text = "Repeat") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .weight(weight = 0.5f),
                onValueChange = { },
                readOnly = true
            )

            ExposedDropdownMenu(
                expanded = frequencyExpanded,
                onDismissRequest = { frequencyExpanded = false }) {
                intArrayOf(1, 2, 4).forEach {
                    DropdownMenuItem(text = { Text(text = "${it}w") }, onClick = {
                        frequencyExpanded = false
                        onFrequencyChanged(it)
                    })
                }
            }
        }
    }

    Row {
        LinearProgressIndicator(
            progress = (days.toFloat() + (7 * weeks)) / (7 * frequency),
            modifier = Modifier.weight(1.0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderDetailsPreview() {
    ReminderTheme {
        Column {
//            ReminderDetailsScreen(navigateToMain = {})
//            ReminderDetails("0",
//                name = "Bunga",
//                start = DayOfWeek.TUESDAY,
//                frequency = 7,
//                progress = 4,
//                onNameChanged = {},
//                onStartChanged = {},
//                onFrequencyChanged = {},
//                onDeletePressed = {})
        }
    }
}