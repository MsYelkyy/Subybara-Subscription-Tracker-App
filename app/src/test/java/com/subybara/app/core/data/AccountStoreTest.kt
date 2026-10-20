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
        assertEquals("Manuel Garcia", user.name)
        assertEquals("capyfriend@email.com", user.email)
        assertEquals("M", user.initial)
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
