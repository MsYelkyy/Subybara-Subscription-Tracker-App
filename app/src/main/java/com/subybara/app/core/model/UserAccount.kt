package com.subybara.app.core.model

/**
 * Represents a user account in the Subybara application.
 *
 * @property name Full name of the user.
 * @property email Email address of the user.
 */
data class UserAccount(
    val name: String,
    val email: String,
) {
    /**
     * Extracts the uppercase initial character of the user's name, or 'U' as fallback.
     */
    val initial: String
        get() = name.trim().firstOrNull()?.uppercase() ?: "U"
}
