package com.subybara.app.core.model

/**
 * Represents one subscription recorded by the user.
 */

enum class SubscriptionStatus {
    ACTIVE,
    PAUSED,
    CANCELED
}

enum class SubscriptionCategory(
    val label: String
) {
    MUSIC("Music"),
    ENTERTAINMENT("Entertainment"),
    PRODUCTIVITY("Productivity"),
    EDUCATION("Education"),
    GAMING("Gaming"),
    HEALTH_FITNESS("Health & Fitness"),
    NEWS_READING("News & Reading"),
    CLOUD_STORAGE("Cloud Storage"),
    UTILITIES("Utilities"),
    OTHERS("Others")
}
data class Subscription(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val billingCycle: String,
    val renewalDate: String,
    val category: SubscriptionCategory,
    val status: SubscriptionStatus,
    val paymentMethod: String = "",
    val startDate: String = "",
    val notes: String = "",
    val logoUri: String? = null
)