package com.lavender.reminder.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lavender.reminder.data.reminder.Reminder
import com.lavender.reminder.data.reminder.ReminderListModel
import com.lavender.reminder.ui.theme.ReminderTheme

@Composable
fun ReminderListScreen(
    viewModel: ReminderListModel = hiltViewModel(), onNavigateToDetails: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ReminderList(reminders = uiState.reminders, onNavigateToDetails = onNavigateToDetails)
}

@Composable
fun ReminderList(reminders: List<Reminder>, onNavigateToDetails: (String) -> Unit) {
    Scaffold(floatingActionButton = {
        Column(
            modifier = Modifier.padding(15.0.dp),
            verticalArrangement = Arrangement.spacedBy(15.0.dp)
        ) {
            FloatingActionButton(onClick = { onNavigateToDetails("0") }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(reminders) { reminder ->
                ReminderRow(reminder = reminder, onNavigateToDetails = onNavigateToDetails)
            }
        }
    }
}

@Composable
fun ReminderRow(reminder: Reminder, onNavigateToDetails: (String) -> Unit) {
    Row(modifier = Modifier.clickable(onClick = { onNavigateToDetails(reminder.uuid) })) {
        Text(text = reminder.name, fontSize = 30.sp, modifier = Modifier.weight(1.0f))
        Text(text = "${reminder.start} in ${(7 * reminder.frequency) - reminder.progress}d")
    }
    Row {
        LinearProgressIndicator(
            progress = reminder.progress.toFloat() / (7 * reminder.frequency),
            modifier = Modifier.weight(1.0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderListPreview() {
    ReminderTheme {
        Column {
            ReminderList(reminders = listOf(), onNavigateToDetails = {})
        }
    }
}