package com.subybara.app.core.model

/**
 * Represents one subscription recorded by the user.
 */
data class Subscription(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val billingCycle: String,
    val renewalDate: String
)