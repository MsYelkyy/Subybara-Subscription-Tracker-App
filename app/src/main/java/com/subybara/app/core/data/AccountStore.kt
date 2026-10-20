package com.subybara.app.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.subybara.app.core.model.UserAccount

/**
 * Stores and manages temporary user account data shared across the application.
 *
 * The records remain available while the application is running, but they are
 * reset when the app process closes because this prototype does not use a backend database.
 */
object AccountStore {

    /**
     * Holds the active user account state.
     */
    var currentUser by mutableStateOf(
        UserAccount(
            name = "Harty User",
            email = "harty@subybara.com",
        )
    )
        private set

    /**
     * Updates the active user details.
     */
    fun updateUser(name: String, email: String) {
        currentUser = UserAccount(
            name = name.trim(),
            email = email.trim(),
        )
    }

    /**
     * Resets the active user details to prototype defaults.
     */
    fun reset() {
        currentUser = UserAccount(
            name = "Harty User",
            email = "harty@subybara.com",
        )
    }
}
