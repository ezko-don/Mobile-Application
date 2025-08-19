package com.ecocollect.app.data.model

import com.google.firebase.Timestamp

data class PickupSchedule(
    val id: String = "",
    val userId: String = "",
    val address: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val status: String = "Pending", // e.g., Pending, Confirmed, In-Progress, Completed, Cancelled
    val notes: String? = null,
    val items: List<String> = emptyList() // List of e-waste item types
)
