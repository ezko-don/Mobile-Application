package com.ecocollect.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecocollect.app.data.model.PickupSchedule

@Composable
fun PickupScheduleItem(schedule: PickupSchedule, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Address: ${schedule.address}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${schedule.timestamp.toDate()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${schedule.status}", style = MaterialTheme.typography.bodyMedium)
            if (schedule.items.isNotEmpty()) {
                Text("Items: ${schedule.items.joinToString()}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
