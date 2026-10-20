package com.subybara.app.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountValidatorTest {

    @Test
    fun isValidEmail_acceptsStandardAndLongTLDExtensions() {
        assertTrue(AccountValidator.isValidEmail("user@example.com"))
        assertTrue(AccountValidator.isValidEmail("harty@subybara.com"))
        assertTrue(AccountValidator.isValidEmail("dev@company.technology"))
        assertTrue(AccountValidator.isValidEmail("photo@studio.photography"))
        assertTrue(AccountValidator.isValidEmail("admin@site.online"))
        assertTrue(AccountValidator.isValidEmail("john.doe@sub.domain.co.uk"))
    }

    @Test
    fun isValidEmail_rejectsInvalidFormats() {
        assertFalse(AccountValidator.isValidEmail(""))
        assertFalse(AccountValidator.isValidEmail("   "))
        assertFalse(AccountValidator.isValidEmail("plainaddress"))
        assertFalse(AccountValidator.isValidEmail("@missingusername.com"))
        assertFalse(AccountValidator.isValidEmail("missingdomain@"))
        assertFalse(AccountValidator.isValidEmail("missingat.com"))
    }

    @Test
    fun isValidName_acceptsValidNamesAndCompoundPunctuation() {
        assertTrue(AccountValidator.isValidName("Harty User"))
        assertTrue(AccountValidator.isValidName("Mary-Jane O'Connor"))
        assertTrue(AccountValidator.isValidName("Jean-Luc"))
        assertTrue(AccountValidator.isValidName("John"))
    }

    @Test
    fun isValidName_rejectsPunctuationOnlyAndInvalidChars() {
        assertFalse(AccountValidator.isValidName(""))
        assertFalse(AccountValidator.isValidName("   "))
        assertFalse(AccountValidator.isValidName("-"))
        assertFalse(AccountValidator.isValidName("---"))
        assertFalse(AccountValidator.isValidName("'"))
        assertFalse(AccountValidator.isValidName("'-'"))
        assertFalse(AccountValidator.isValidName("John123"))
        assertFalse(AccountValidator.isValidName("User@Name"))
    }
}
