package com.subybara.app

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.core.app.ActivityOptionsCompat
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.feature.subscriptions.AddSubscriptionScreen
import com.subybara.app.feature.subscriptions.EditSubscriptionScreen
import com.subybara.app.feature.subscriptions.SubscriptionDetailsScreen
import com.subybara.app.feature.subscriptions.SubscriptionsScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Checks that a logo picked in the form is shown, saved, replaced when
 * editing, and displayed on the list and details screens.
 *
 * The system photo picker is replaced by a registry that returns test images.
 */
class CustomLogoTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val pickedLogos = ArrayDeque<Uri?>()

    private val photoPicker = object : ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry =
            object : ActivityResultRegistry() {
                override fun <I, O> onLaunch(
                    requestCode: Int,
                    contract: ActivityResultContract<I, O>,
                    input: I,
                    options: ActivityOptionsCompat?
                ) {
                    dispatchResult(requestCode, pickedLogos.removeFirst())
                }
            }
    }

    private val sample = Subscription(
        id = 1,
        name = "Test Music",
        amount = 149.0,
        billingCycle = "Monthly",
        renewalDate = "October 16, 2026",
        category = SubscriptionCategory.MUSIC,
        status = SubscriptionStatus.ACTIVE,
        paymentMethod = "GCash"
    )

    @Test
    fun pickedLogoIsShownAndSavedWithTheNewSubscription() {
        val logo = createLogoFile("picked_logo.png", Color.RED)
        var saved: Subscription? = null
        pickedLogos.add(logo)

        composeRule.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides photoPicker) {
                SubybaraTheme {
                    AddSubscriptionScreen(onCloseClick = {}, onSaveClick = { saved = it }, onDoneClick = {})
                }
            }
        }

        composeRule.onNodeWithContentDescription("Subscription Name").performTextInput("Study Plan")
        composeRule.onNodeWithText("Add Logo").performClick()
        waitForLogo("Study Plan logo")
        composeRule.onNodeWithText("Add Logo").assertDoesNotExist()

        composeRule.onNodeWithText("Select Category").performScrollTo().performClick()
        composeRule.onNodeWithText("Education").performClick()
        composeRule.onNodeWithContentDescription("Billing Amount").performScrollTo().performTextInput("99.50")
        composeRule.onNodeWithText("Select Payment Method").performScrollTo().performClick()
        composeRule.onNodeWithText("GCash").performClick()
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithText("Save Subscription").performScrollTo().performClick()

        composeRule.onNodeWithText("Added successfully!").assertExists()
        composeRule.runOnIdle { assertEquals(logo.toString(), saved?.logoUri) }
    }

    @Test
    fun editingKeepsTheLogoWhenPickingIsCanceledAndSavesANewOne() {
        val oldLogo = createLogoFile("old_logo.png", Color.RED)
        val newLogo = createLogoFile("new_logo.png", Color.BLUE)
        val subscription = sample.copy(logoUri = oldLogo.toString())
        var saved = subscription
        pickedLogos.addAll(listOf(null, newLogo))

        composeRule.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides photoPicker) {
                SubybaraTheme {
                    EditSubscriptionScreen(subscription, {}, { saved = it })
                }
            }
        }

        waitForLogo("Test Music logo")
        composeRule.onNodeWithContentDescription("Test Music logo").performClick()
        composeRule.onNodeWithContentDescription("Test Music logo").assertExists()
        composeRule.onNodeWithText("Add Logo").assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Test Music logo").performClick()
        waitForLogo("Test Music logo")
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithText("Save Subscription").performScrollTo().performClick()
        composeRule.onNodeWithText("Yes").performClick()

        composeRule.runOnIdle { assertEquals(subscription.copy(logoUri = newLogo.toString()), saved) }
    }

    @Test
    fun uploadedLogoIsShownOnTheListAndDetailsScreens() {
        val logo = createLogoFile("list_logo.png", Color.GREEN)
        val subscription = sample.copy(name = "Study Plan", logoUri = logo.toString())
        var showDetails by mutableStateOf(false)

        composeRule.setContent {
            SubybaraTheme {
                if (showDetails) {
                    SubscriptionDetailsScreen(subscription, {}, {}, {}, {})
                } else {
                    SubscriptionsScreen(listOf(subscription), {}, {}, {})
                }
            }
        }

        waitForLogo("Study Plan logo")
        composeRule.onNodeWithText("S").assertDoesNotExist()

        composeRule.runOnIdle { showDetails = true }
        waitForLogo("Study Plan logo")
        composeRule.onNodeWithText("S").assertDoesNotExist()
    }

    private fun createLogoFile(name: String, color: Int): Uri {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, name)
        val bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }
        file.outputStream().use { stream -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) }
        return Uri.fromFile(file)
    }

    private fun waitForLogo(description: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
