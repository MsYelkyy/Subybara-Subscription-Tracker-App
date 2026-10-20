package com.subybara.app.core.data

import androidx.compose.runtime.mutableStateListOf
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
/**
 * Stores and manages temporary subscription data shared across the application.
 *
 * The records remain available while the application is running, but they are
 * reset when the app process closes because MS2 does not use a database.
 */
object SubscriptionStore {

    private val _subscriptions = mutableStateListOf(
        Subscription(
            id = 1,
            name = "Spotify Premium",
            amount = 149.00,
            billingCycle = "Monthly",
            renewalDate = "October 29, 2026",
            category = SubscriptionCategory.MUSIC,
            status = SubscriptionStatus.ACTIVE,
            paymentMethod = "Credit Card",
            startDate = "March 29, 2024"
        ),
        Subscription(
            id = 2,
            name = "Netflix",
            amount = 549.00,
            billingCycle = "Monthly",
            renewalDate = "November 1, 2026",
            category = SubscriptionCategory.ENTERTAINMENT,
            status = SubscriptionStatus.ACTIVE,
            paymentMethod = "GCash",
            startDate = "January 24, 2023",
            notes = "All apps plan"
        ),
        Subscription(
            id = 3,
            name = "YouTube Premium",
            amount = 159.00,
            billingCycle = "Monthly",
            renewalDate = "November 8, 2026",
            category = SubscriptionCategory.MUSIC,
            status = SubscriptionStatus.ACTIVE,
            paymentMethod = "Maya",
            startDate = "August 8, 2025"
        )
    )

    /**
     * Provides read-only access to the current subscriptions.
     */
    val subscriptions: List<Subscription>
        get() = _subscriptions

    /**
     * Finds a subscription using its unique ID.
     */
    fun findById(subscriptionId: Int): Subscription? {
        return _subscriptions.find { subscription ->
            subscription.id == subscriptionId
        }
    }

    /**
     * Adds a subscription and automatically assigns its next available ID.
     */
    fun add(subscription: Subscription) {
        val nextId = (_subscriptions.maxOfOrNull { subscriptionItem ->
            subscriptionItem.id
        } ?: 0) + 1

        _subscriptions.add(
            subscription.copy(id = nextId)
        )
    }

    /**
     * Replaces an existing subscription with updated information.
     *
     * @return `true` when the subscription was found and updated.
     */
    fun update(updatedSubscription: Subscription): Boolean {
        val index = _subscriptions.indexOfFirst { subscription ->
            subscription.id == updatedSubscription.id
        }

        if (index == -1) {
            return false
        }

        _subscriptions[index] = updatedSubscription
        return true
    }

    /** Updates tracking status only; it does not contact the provider. */
    fun changeStatus(subscriptionId: Int, status: SubscriptionStatus): Boolean {
        val subscription = findById(subscriptionId) ?: return false
        return update(subscription.copy(status = status))
    }

    /**
     * Removes a subscription using its unique ID.
     *
     * @return `true` when a matching subscription was removed.
     */
    fun remove(subscriptionId: Int): Boolean {
        return _subscriptions.removeAll { subscription ->
            subscription.id == subscriptionId
        }
    }

    /**
     * Puts back a subscription that was just removed, at its previous position.
     *
     * Does nothing if a subscription with the same ID is already in the list.
     */
    fun restore(subscription: Subscription, position: Int) {
        if (findById(subscription.id) != null) {
            return
        }

        _subscriptions.add(
            position.coerceIn(0, _subscriptions.size),
            subscription
        )
    }
}