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
    private val users = mutableStateMapOf<String, UserAccount>()
    private fun accountKey(email: String) = email.trim().lowercase(java.util.Locale.ROOT)

    fun signIn(email: String, password: String): Boolean {
        val key = accountKey(email)
        if (key == DemoEmail && password != DemoPassword) return false
        currentUser = users.getOrPut(key) {
            UserAccount(if (key == DemoEmail) "MS2 Demo" else "Capy Friend", key)
        }
        SubscriptionStore.openAccount(key) { if (key == DemoEmail) demoSubscriptions() else emptyList() }
        return true
    }

    fun register(name: String, email: String): Boolean {
        val key = accountKey(email)
        if (key == DemoEmail || users.containsKey(key)) return false
        currentUser = UserAccount(name.trim(), key)
        users[key] = currentUser
        SubscriptionStore.openAccount(key)
        return true
    }

    fun signOut() {
        SubscriptionStore.closeAccount()
        currentUser = UserAccount("Manuel Garcia", "capyfriend@email.com")
    }

    /**
     * Holds the active user account state.
     */
    var currentUser by mutableStateOf(
        UserAccount(
            name = "Manuel Garcia",
            email = "capyfriend@email.com",
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
        users.clear()
        SubscriptionStore.resetAccounts()
        currentUser = UserAccount(
            name = "Manuel Garcia",
            email = "capyfriend@email.com",
        )
    }
}
