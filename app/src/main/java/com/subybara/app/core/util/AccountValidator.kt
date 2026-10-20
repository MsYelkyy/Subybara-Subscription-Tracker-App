package com.subybara.app.core.util

/**
 * Utility functions for validating user account input fields.
 */
object AccountValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    /**
     * Validates that an email address follows standard format and allows top-level domains of any length >= 2.
     *
     * @param email The email string to validate.
     * @return `true` if valid, `false` otherwise.
     */
    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.isNotEmpty() && trimmed.matches(EMAIL_REGEX)
    }

    /**
     * Validates that a user name is non-empty, contains at least one letter, and consists only of valid characters
     * (letters, spaces, hyphens, and apostrophes). Rejects names composed solely of punctuation or whitespace.
     *
     * @param name The name string to validate.
     * @return `true` if valid, `false` otherwise.
     */
    fun isValidName(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return false
        val containsLetter = trimmed.any { it.isLetter() }
        val validCharsOnly = trimmed.all { (it.isLetter() || it.isWhitespace() || it == '\'' || it == '-') }
        return containsLetter && validCharsOnly
    }
}
