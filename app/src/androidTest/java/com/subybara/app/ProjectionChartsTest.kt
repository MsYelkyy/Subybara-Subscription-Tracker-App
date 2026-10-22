package com.subybara.app

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.*
import com.subybara.app.navigation.SubybaraNavHost
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class ProjectionChartsTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var original: List<Subscription>

    @Before fun setUp() {
        original = SubscriptionStore.subscriptions.toList()
        original.forEach { SubscriptionStore.remove(it.id) }
        compose.setContent { SubybaraTheme { SubybaraNavHost() } }
        compose.signInForTest()
    }

    @After fun tearDown() {
        compose.runOnIdle {
            SubscriptionStore.subscriptions.toList().forEach { SubscriptionStore.remove(it.id) }
            original.forEachIndexed { index, item -> SubscriptionStore.restore(item, index) }
        }
    }

    private fun add(amount: Double = 100.0): Int {
        var id = 0
        compose.runOnIdle {
            SubscriptionStore.add(Subscription(name = "Chart fixture", amount = amount,
                billingCycle = "Monthly", renewalDate = formatDate(todayInMillis()),
                category = SubscriptionCategory.MUSIC, status = SubscriptionStatus.ACTIVE))
            id = SubscriptionStore.subscriptions.last().id
        }
        return id
    }

    private fun chartDescription(tag: String): String = compose.onNodeWithTag(tag)
        .performScrollTo().assertIsDisplayed().fetchSemanticsNode()
        .config[SemanticsProperties.ContentDescription].single()

    @Test fun homeAndInsightsExposeExactlyTheSameSixMonthlyValues() {
        add()
        val home = chartDescription("projection-line")
        assertTrue(home.contains(formatSubscriptionAmount(100.0)))
        assertEquals(6, home.split(": ").size - 1)
        compose.onNodeWithText("Insights").performClick()
        assertEquals(home, chartDescription("projection-bar"))
        compose.onNodeWithText("Next 6 Months").performClick()
        val months = SubscriptionStore.subscriptions.projectedMonthlyBills()
        compose.onNodeWithText("${months.first().fullLabel} – ${months.last().fullLabel}").assertIsDisplayed()
    }

    @Test fun chartsReactToAddEditPauseResumeCancelDeleteWithoutLeavingScreen() {
        compose.onNodeWithTag("projection-empty").performScrollTo().assertIsDisplayed()
        val id = add()
        assertTrue(chartDescription("projection-line").contains(formatSubscriptionAmount(100.0)))
        compose.onNodeWithText("Insights").performClick()
        compose.runOnIdle {
            SubscriptionStore.update(requireNotNull(SubscriptionStore.findById(id)).copy(amount = 250.0))
        }
        assertTrue(chartDescription("projection-bar").contains(formatSubscriptionAmount(250.0)))
        compose.runOnIdle { SubscriptionStore.changeStatus(id, SubscriptionStatus.PAUSED) }
        compose.onNodeWithTag("projection-empty").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { SubscriptionStore.changeStatus(id, SubscriptionStatus.ACTIVE) }
        chartDescription("projection-bar")
        compose.runOnIdle { SubscriptionStore.changeStatus(id, SubscriptionStatus.CANCELED) }
        compose.onNodeWithTag("projection-empty").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { SubscriptionStore.remove(id) }
        compose.onNodeWithText("Categories").performClick()
        compose.onNodeWithTag("category-empty").assertIsDisplayed()
        compose.onNodeWithTag("top-category").assertDoesNotExist()
    }

    @Test fun tabsShowStackedCategoriesProgressAndTopSummary() {
        add()
        compose.onNodeWithText("Insights").performClick()
        compose.onNodeWithText("Spending").performClick()
        chartDescription("projection-bar")
        compose.onNodeWithTag("category-stacked-bar").assertDoesNotExist()
        compose.onNodeWithText("Categories").performClick()
        compose.onNodeWithTag("projection-bar").assertDoesNotExist()
        compose.onNodeWithTag("category-stacked-bar").assertIsDisplayed()
            .assertContentDescriptionEquals("Music: 100%")
        compose.onNodeWithText("Music · 100%").assertIsDisplayed()
        compose.onNodeWithText("Top: Music").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("100% of estimated monthly cost").assertIsDisplayed()
    }

    @Test fun zeroAmountHasFlatChartsAndNoMisleadingTopCategory() {
        add(0.0)
        chartDescription("projection-line")
        compose.onNodeWithText("Insights").performClick()
        chartDescription("projection-bar")
        compose.onNodeWithText("Categories").performClick()
        compose.onNodeWithText("No estimated monthly cost yet.").assertIsDisplayed()
        compose.onNodeWithTag("category-stacked-bar").assertContentDescriptionEquals("Music: 0%")
        compose.onNodeWithTag("top-category").assertDoesNotExist()
    }

    @Test fun mixedBillingCyclesRenderWithCategoryTotalsAndCapturePreviews() {
        compose.runOnIdle {
            val fixtures = listOf(
                Triple(SubscriptionCategory.ENTERTAINMENT, "Monthly", 549.0),
                Triple(SubscriptionCategory.PRODUCTIVITY, "Yearly", 2600.0),
                Triple(SubscriptionCategory.MUSIC, "Monthly", 149.0),
                Triple(SubscriptionCategory.CLOUD_STORAGE, "Quarterly", 390.0),
                Triple(SubscriptionCategory.OTHERS, "Weekly", 50.0)
            )
            fixtures.forEachIndexed { index, (category, cycle, amount) ->
                SubscriptionStore.add(Subscription(name = category.label, amount = amount,
                    billingCycle = cycle, renewalDate = formatDate(addBillingCycles(todayInMillis(), "Monthly", index % 3)),
                    category = category, status = SubscriptionStatus.ACTIVE))
            }
        }
        val home = chartDescription("projection-line")
        capturePreview("home-chart")
        compose.onNodeWithText("Insights").performClick()
        assertEquals(home, chartDescription("projection-bar"))
        capturePreview("insights-trend")
        compose.onNodeWithText("Categories").performClick()
        compose.onNodeWithTag("category-stacked-bar").assertIsDisplayed()
        compose.onNodeWithText("Top: Entertainment").performScrollTo().assertIsDisplayed()
        capturePreview("insights-categories")
    }

    private fun capturePreview(name: String) {
        val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chart-previews")
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use { output ->
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }
}
