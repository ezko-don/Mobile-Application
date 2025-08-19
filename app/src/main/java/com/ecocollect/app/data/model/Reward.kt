package com.ecocollect.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey
    @DocumentId
    val rewardId: String = "",
    val title: String = "",
    val description: String = "",
    val type: RewardType = RewardType.COUPON,
    val pointsCost: Int = 0,
    val partnerLogoUrl: String? = null,
    val partnerName: String = "",
    val isActive: Boolean = true,
    val expiryDays: Int = 30,
    val termsAndConditions: String = "",
    val category: RewardCategory = RewardCategory.SHOPPING
)

enum class RewardType {
    COUPON,
    DONATION,
    TRANSIT_CREDIT,
    GIFT_CARD,
    DISCOUNT
}

enum class RewardCategory {
    SHOPPING,
    FOOD,
    TRANSPORT,
    CHARITY,
    ENTERTAINMENT,
    EDUCATION
}

@Entity(tableName = "reward_transactions")
data class RewardTransaction(
    @PrimaryKey
    val transactionId: String = "",
    val userId: String = "",
    val rewardId: String = "",
    val pointsSpent: Int = 0,
    val couponCode: String? = null,
    val redeemedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0,
    val isUsed: Boolean = false,
    val usedAt: Long? = null
)
