package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ecocollect.app.data.model.PickupSchedule
import com.ecocollect.app.ui.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel(), navController: NavController) {
    val schedules by viewModel.schedules.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.NewSchedule.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Schedule Pickup")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(schedules) { schedule ->
                PickupScheduleItem(schedule = schedule)
            }
        }
    }
}

@Composable
fun PickupScheduleItem(schedule: PickupSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Address: ${schedule.address}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${schedule.timestamp.toDate()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${schedule.status}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
