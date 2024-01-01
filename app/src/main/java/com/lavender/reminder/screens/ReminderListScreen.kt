package com.lavender.reminder.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        Column(
            modifier = Modifier.padding(25.dp), verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            FloatingActionButton(onClick = { onNavigateToDetails("0") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(uiState.reminders) { reminder ->
                ReminderRow(reminder = reminder, onNavigateToDetails = onNavigateToDetails)
            }
        }
    }
}

@Composable
fun ReminderRow(reminder: Reminder, onNavigateToDetails: (String) -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = { onNavigateToDetails(reminder.uuid) })
    ) {
        val days = reminder.progress.first
        val weeks = reminder.progress.second
        val progress = days + (7 * weeks)

        Row {
            Text(text = reminder.name, fontSize = 30.sp, modifier = Modifier.weight(1.0f))
            Column {
                Text(text = "${reminder.start.name.lowercase()} in ${(7 * reminder.frequency) - progress}d")
                Text(text = "${reminder.frequency}w", modifier = Modifier.align(Alignment.End))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row {
            LinearProgressIndicator(
                progress = progress.toFloat() / (7 * reminder.frequency),
                modifier = Modifier
                    .fillMaxSize()
                    .height(15.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderListPreview() {
    ReminderTheme {
        Column {
//            ReminderList(reminders = listOf(), onNavigateToDetails = {})
        }
    }
}