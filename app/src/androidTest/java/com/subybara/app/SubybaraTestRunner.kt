package com.subybara.app

import androidx.test.runner.AndroidJUnitRunner
import com.subybara.app.navigation.navigationCooldownMillis

/** UI tests tap faster than a person, so the rapid-tap guard is off unless a test enables it. */
class SubybaraTestRunner : AndroidJUnitRunner() {
    override fun onStart() {
        navigationCooldownMillis = 0L
        super.onStart()
    }
}
