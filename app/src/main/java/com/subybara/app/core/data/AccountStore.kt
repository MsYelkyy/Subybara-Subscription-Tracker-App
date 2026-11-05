package com.subybara.app.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import com.subybara.app.core.model.UserAccount

/**
 * Stores and manages temporary user account data shared across the application.
 *
 * The records remain available while the application is running, but they are
 * reset when the app process closes because this prototype does not use a backend database.
 */
object AccountStore {
    const val DemoEmail = "test@email.com"
    const val DemoPassword = "Test1234!"
    private val DefaultUser = UserAccount(
        name = "Manuel Garcia",
        email = "capyfriend@email.com",
    )
    private val users = mutableStateMapOf<String, UserAccount>()
    private val passwords = mutableMapOf<String, String>()
    private fun accountKey(email: String) = email.trim().lowercase(java.util.Locale.ROOT)

    /**
     * Opens the account only when the email is registered and the password matches
     * the one chosen at sign up.
     */
    fun signIn(email: String, password: String): Boolean {
        val key = accountKey(email)
        val savedPassword = if (key == DemoEmail) DemoPassword else passwords[key]
        if (savedPassword == null || password != savedPassword) return false
        currentUser = users.getOrPut(key) { UserAccount("MS2 Demo", key) }
        SubscriptionStore.openAccount(key) { if (key == DemoEmail) demoSubscriptions() else emptyList() }
        return true
    }

    fun register(name: String, email: String, password: String): Boolean {
        val key = accountKey(email)
        if (key == DemoEmail || users.containsKey(key)) return false
        currentUser = UserAccount(name.trim(), key)
        users[key] = currentUser
        passwords[key] = password
        SubscriptionStore.openAccount(key)
        return true
    }

    fun signOut() {
        SubscriptionStore.closeAccount()
        currentUser = DefaultUser
    }

    /**
     * Holds the active user account state.
     */
    var currentUser by mutableStateOf(DefaultUser)
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
        users.clear()
        passwords.clear()
        SubscriptionStore.resetAccounts()
        currentUser = DefaultUser
    }
}
