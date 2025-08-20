package com.ecocollect.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PickupSchedule(
    @DocumentId val id: String = "",
    val userId: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @ServerTimestamp val timestamp: Date = Date(),
    val status: String = "Scheduled", // e.g., Pending, Confirmed, In-Progress, Completed, Cancelled
    val notes: String? = null,
    val items: List<String> = emptyList() // List of e-waste item types
)
