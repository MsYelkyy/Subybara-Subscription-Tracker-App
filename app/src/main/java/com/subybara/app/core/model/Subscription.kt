package com.subybara.app.core.model

/**
 * Represents one subscription recorded by the user.
 */

enum class SubscriptionStatus {
    ACTIVE,
    PAUSED,
    CANCELED
}
data class Subscription(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val billingCycle: String,
    val renewalDate: String,
    val status: SubscriptionStatus
)