package com.ecocollect.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Schedule(
    val id: String = "",
    val date: String = "",
    val items: List<String> = emptyList(),
    val status: String = ""
)

@Composable
fun HistoryScreen() {
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("schedules")
            .whereEqualTo("status", "completed")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                schedules = result.documents.map { doc ->
                    Schedule(
                        id = doc.id,
                        date = doc.getString("date") ?: "",
                        items = doc.get("items") as? List<String> ?: emptyList(),
                        status = doc.getString("status") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else if (schedules.isEmpty()) {
                Text(
                    "No completed pickups found.",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(schedules) { schedule ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Date: ${schedule.date}", style = MaterialTheme.typography.titleMedium)
                                Text("Items: ${schedule.items.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}