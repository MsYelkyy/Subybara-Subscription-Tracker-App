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

    @Test fun disneyPlusHasItsOwnLogoOutsideTheDemoData() {
        val disney = logoFor("Disney+")
        assertNotNull(disney)
        assertEquals(disney, logoFor("disney plus premium"))
        assertFalse(demoSubscriptions().map { logoFor(it.name) }.contains(disney))
    }

    @Test fun demoAndTwoNewUsersRemainIsolated() {
        AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword)
        val demo = SubscriptionStore.subscriptions.toList()
        AccountStore.signOut()
        assertTrue(AccountStore.register("Ana Cruz", "ana@example.com", "secure123"))
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        SubscriptionStore.add(demo.first().copy(name = "Ana subscription"))
        AccountStore.signOut()
        assertTrue(AccountStore.register("Ben Cruz", "ben@example.com", "benpass1"))
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        assertTrue(AccountStore.signIn("ana@example.com", "secure123"))
        assertEquals("Ana subscription", SubscriptionStore.subscriptions.single().name)
        assertEquals("Ana Cruz", AccountStore.currentUser.name)
        AccountStore.signIn(AccountStore.DemoEmail, AccountStore.DemoPassword)
        assertEquals(demo, SubscriptionStore.subscriptions)
    }

    @Test fun incorrectDemoPasswordAndDuplicateRegistrationDoNotSwitchOrEraseData() {
        AccountStore.register("Ana Cruz", "ana@example.com", "secure123")
        val user = AccountStore.currentUser
        assertFalse(AccountStore.signIn(AccountStore.DemoEmail, "wrong"))
        assertEquals(user, AccountStore.currentUser)
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        assertFalse(AccountStore.register("Demo", " TEST@EMAIL.COM ", "secure123"))
        assertFalse(AccountStore.register("Other", " ANA@example.com ", "other123"))
        assertEquals(user, AccountStore.currentUser)
    }

    @Test fun signInRequiresTheRegisteredPassword() {
        AccountStore.register("Ana Cruz", "ana@example.com", "secure123")
        SubscriptionStore.add(demoSubscriptions().first())
        AccountStore.signOut()
        val signedOutUser = AccountStore.currentUser
        assertFalse(AccountStore.signIn("ana@example.com", "wrong123"))
        assertFalse(AccountStore.signIn("ana@example.com", "SECURE123"))
        assertFalse(AccountStore.signIn("nobody@example.com", "secure123"))
        assertEquals(signedOutUser, AccountStore.currentUser)
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
        assertTrue(AccountStore.signIn(" ANA@example.com ", "secure123"))
        assertEquals("Ana Cruz", AccountStore.currentUser.name)
        assertEquals(1, SubscriptionStore.subscriptions.size)
    }

    @Test fun duplicateRegistrationDoesNotReplaceThePassword() {
        AccountStore.register("Ana Cruz", "ana@example.com", "secure123")
        AccountStore.signOut()
        assertFalse(AccountStore.register("Other", "ana@example.com", "hijack123"))
        assertFalse(AccountStore.signIn("ana@example.com", "hijack123"))
        assertTrue(AccountStore.signIn("ana@example.com", "secure123"))
    }
}
