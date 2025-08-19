package com.ecocollect.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ecocollect.app.data.local.Converters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "pickups")
@TypeConverters(Converters::class)
data class Pickup(
    @PrimaryKey
    @DocumentId
    val pickupId: String = "",
    val userId: String = "",
    val status: PickupStatus = PickupStatus.REQUESTED,
    val items: List<EWasteItem> = emptyList(),
    val weightKg: Double = 0.0,
    val scheduledAt: Timestamp? = null,
    val collectedAt: Timestamp? = null,
    val rewardPoints: Int = 0,
    val courierId: String? = null,
    val pickupAddress: Address? = null,
    val estimatedETA: String? = null,
    val courierName: String? = null,
    val notes: String? = null,
    val photoUrls: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

enum class PickupStatus {
    REQUESTED,
    CONFIRMED,
    EN_ROUTE,
    COLLECTED,
    CANCELLED
}

data class EWasteItem(
    val category: EWasteCategory,
    val quantity: Int,
    val description: String? = null,
    val estimatedWeight: Double = 0.0
)

enum class EWasteCategory(val displayName: String, val bonusMultiplier: Double) {
    PHONE("Mobile Phone", 1.3),
    LAPTOP("Laptop", 1.2),
    TABLET("Tablet", 1.2),
    BATTERY("Battery", 1.5),
    CHARGER("Charger", 1.1),
    HEADPHONES("Headphones", 1.1),
    CAMERA("Camera", 1.2),
    GAMING_CONSOLE("Gaming Console", 1.2),
    SMART_WATCH("Smart Watch", 1.3),
    OTHER("Other", 1.0)
}

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val landmark: String? = null
)
