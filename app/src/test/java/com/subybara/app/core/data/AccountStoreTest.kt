package com.subybara.app.core.data

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AccountStoreTest {

    @Before
    fun setUp() {
        AccountStore.reset()
    }

    @Test
    fun initialAccountState_hasDefaultValues() {
        val user = AccountStore.currentUser
        assertEquals("Harty User", user.name)
        assertEquals("harty@subybara.com", user.email)
        assertEquals("H", user.initial)
    }

    @Test
    fun updateUser_updatesStateAndInitial() {
        AccountStore.updateUser("  Mary-Jane O'Connor  ", "  mary.jane@subybara.technology  ")
        val updatedUser = AccountStore.currentUser

        assertEquals("Mary-Jane O'Connor", updatedUser.name)
        assertEquals("mary.jane@subybara.technology", updatedUser.email)
        assertEquals("M", updatedUser.initial)
    }
}
