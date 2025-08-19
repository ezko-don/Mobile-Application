package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScheduleScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to new schedule screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Schedule Pickup")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            // TODO: Display list of scheduled pickups
        }
    }
}
