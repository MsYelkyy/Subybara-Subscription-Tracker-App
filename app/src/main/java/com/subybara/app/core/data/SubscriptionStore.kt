package com.subybara.app.core.data

import androidx.compose.runtime.mutableStateListOf
import com.subybara.app.core.model.Subscription
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
            renewalDate = "October 29",
            status = SubscriptionStatus.ACTIVE
        ),
        Subscription(
            id = 2,
            name = "Netflix",
            amount = 549.00,
            billingCycle = "Monthly",
            renewalDate = "November 1",
            status = SubscriptionStatus.ACTIVE
        ),
        Subscription(
            id = 3,
            name = "YouTube Premium",
            amount = 159.00,
            billingCycle = "Monthly",
            renewalDate = "November 8",
            status = SubscriptionStatus.ACTIVE
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
}