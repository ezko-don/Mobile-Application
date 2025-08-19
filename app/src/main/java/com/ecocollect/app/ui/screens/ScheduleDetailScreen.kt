package com.ecocollect.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable

@Composable
fun ScheduleDetailScreen(
    scheduleId: String,
    eWasteItems: List<String>,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Details") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Map view placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Map View Placeholder")
            }

            Text("E-Waste Items:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(eWasteItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Schedule")
            }
        }
    }
}

// In your navigation setup, pass scheduleId and eWasteItems as needed
composable("scheduleDetail/{scheduleId}") { backStackEntry ->
    val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: ""
    val eWasteItems = listOf("Laptop", "Mobile Phone", "Printer") // Replace with actual data
    ScheduleDetailScreen(
        scheduleId = scheduleId,
        eWasteItems = eWasteItems,
        onCancel = { /* handle cancel logic */ }
    )
}