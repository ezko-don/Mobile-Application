package com.ecocollect.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val totalPoints: Int = 0,
    val co2SavedKg: Double = 0.0,
    val createdAt: Timestamp? = null,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val isActive: Boolean = true
)
