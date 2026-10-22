package com.subybara.app.core.data

import com.subybara.app.core.model.*
import com.subybara.app.feature.subscriptions.logoFor
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DemoAccountTest {
    @Before fun before() = AccountStore.reset()
    @After fun after() = AccountStore.reset()

    @Test fun demoSeedsOnceAndRetainsEditsAndDeletions() {
        assertTrue(AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword))
        val original = SubscriptionStore.subscriptions.toList()
        assertEquals(7, original.size)
        assertTrue(original.upcomingRenewals(withinDays = 7).isNotEmpty())
        assertTrue(original.subscriptionChartData().months.any { it.amount > 0 })
        assertEquals(setOf("Weekly", "Monthly", "Quarterly", "Yearly"), original.map { it.billingCycle }.toSet())
        SubscriptionStore.update(original.first().copy(amount = 123.0))
        SubscriptionStore.remove(original.last().id)
        AccountStore.signOut()
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        AccountStore.signIn(" TEST@EMAIL.COM ", AccountStore.DemoPassword)
        assertEquals(6, SubscriptionStore.subscriptions.size)
        assertEquals(123.0, SubscriptionStore.subscriptions.first().amount, 0.0)
        SubscriptionStore.subscriptions.toList().forEach { SubscriptionStore.remove(it.id) }
        AccountStore.signOut()
        AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword)
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
    }

    @Test fun everyDemoSubscriptionHasItsOwnAppLogo() {
        val logos = demoSubscriptions().map { logoFor(it.name) }
        assertTrue(logos.none { it == null })
        assertEquals(logos.size, logos.toSet().size)
    }

    @Test fun demoAndTwoNewUsersRemainIsolated() {
        AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword)
        val demo = SubscriptionStore.subscriptions.toList()
        AccountStore.signOut()
        assertTrue(AccountStore.register("Ana Cruz", "ana@example.com"))
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        SubscriptionStore.add(demo.first().copy(name = "Ana subscription"))
        AccountStore.signOut()
        assertTrue(AccountStore.register("Ben Cruz", "ben@example.com"))
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        AccountStore.signIn("ana@example.com", "simulated")
        assertEquals("Ana subscription", SubscriptionStore.subscriptions.single().name)
        assertEquals("Ana Cruz", AccountStore.currentUser.name)
        AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword)
        assertEquals(demo, SubscriptionStore.subscriptions)
    }

    @Test fun incorrectDemoPasswordAndDuplicateRegistrationDoNotSwitchOrEraseData() {
        AccountStore.register("Ana Cruz", "ana@example.com")
        val user = AccountStore.currentUser
        assertFalse(AccountStore.signIn(AccountStore.DemoEmail, "wrong"))
        assertEquals(user, AccountStore.currentUser)
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        assertFalse(AccountStore.register("Demo", " TEST@EMAIL.COM "))
        assertFalse(AccountStore.register("Other", " ANA@example.com "))
        assertEquals(user, AccountStore.currentUser)
    }
}
