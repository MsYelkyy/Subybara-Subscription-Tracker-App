package com.subybara.app.feature.subscriptions

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraFilterChip
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraSearchField
import com.subybara.app.core.designsystem.theme.Neutral100
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary200
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.WarmBrown
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.nextBillingDate
import com.subybara.app.navigation.SubybaraDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val SubscriptionRowPressedColor = Neutral100.copy(alpha = 0.6f)

/**
 * Lists the user's subscriptions with search and status filter.
 *
 * @param subscriptions Subscriptions to show, usually from SubscriptionStore.
 * @param onSubscriptionClick Called when a subscription row is tapped.
 * @param onDestinationClick Called when a bottom bar destination is tapped.
 * @param onAddSubscriptionClick Called when the center add button is tapped.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun SubscriptionsScreen(
    subscriptions: List<Subscription>,
    onSubscriptionClick: (Subscription) -> Unit,
    onDestinationClick: (SubybaraDestination) -> Unit,
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable {
        mutableStateOf(SubscriptionFilter.All)
    }

    val visibleSubscriptions = filterSubscriptions(subscriptions, searchQuery, selectedFilter)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SubybaraBottomBar(
                selectedDestination = SubybaraDestination.Subscriptions,
                onDestinationClick = onDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )
        }
    ) { innerPadding ->

        // Show the empty state only when there are no saved subscriptions.
        if (subscriptions.isEmpty()) {
            EmptySubscriptionsState(
                onAddSubscriptionClick = onAddSubscriptionClick,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp
                    )
            ) {
                Text(
                    text = "My Subscription",
                    Modifier
                        .fillMaxWidth()
                        .semantics { heading() },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Neutral800
                )

                Spacer(modifier = Modifier.height(20.dp))

                SubybaraSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search subscriptions"
                )

                SubscriptionFilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedFilter.label} (${visibleSubscriptions.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (visibleSubscriptions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "No matching subscriptions",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "No subscriptions match your search or status filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        // Offer to clear only when the user typed a search; a status chip
                        // alone is changed by tapping another chip.
                        if (searchQuery.isNotBlank()) {
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedFilter = SubscriptionFilter.All
                            }) { Text("Clear Filters") }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = visibleSubscriptions,
                            key = { subscription -> subscription.id }
                        ) { subscription ->
                            SubscriptionRow(
                                subscription = subscription,
                                onClick = { onSubscriptionClick(subscription) }
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun filterSubscriptions(
    subscriptions: List<Subscription>,
    query: String,
    filter: SubscriptionFilter
): List<Subscription> = subscriptions.filter { subscription ->
    subscription.name.contains(query.trim(), ignoreCase = true) && when (filter) {
        SubscriptionFilter.All -> true
        SubscriptionFilter.Active -> subscription.status == SubscriptionStatus.ACTIVE
        SubscriptionFilter.Paused -> subscription.status == SubscriptionStatus.PAUSED
        SubscriptionFilter.Canceled -> subscription.status == SubscriptionStatus.CANCELED
    }
}

// Keep the content centered and allow scrolling on short screens.
@Composable
private fun EmptySubscriptionsState(
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.capy_no_subscription),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No subscriptions yet",
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add your first subscription to start tracking renewals.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                ),
                color = WarmBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            SubybaraPrimaryButton(
                text = "Add Subscription",
                onClick = onAddSubscriptionClick,
                modifier = Modifier.heightIn(min = 48.dp),
                shape = RoundedCornerShape(22.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

internal enum class SubscriptionFilter(
    val label: String
) {
    All("All"),
    Active("Active"),
    Paused("Paused"),
    Canceled("Canceled")
}

@Composable
private fun SubscriptionFilterChips(
    selectedFilter: SubscriptionFilter,
    onFilterSelect: (SubscriptionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubscriptionFilter.entries.forEach { filter ->
            SubybaraFilterChip(
                label = filter.label,
                selected = filter == selectedFilter,
                onClick = { onFilterSelect(filter) }
            )
        }
    }
}

@Composable
private fun SubscriptionRow(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPressed) SubscriptionRowPressedColor else Color.Transparent)
            .reportPressesTo(interactionSource)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(
            name = subscription.name,
            logoUri = subscription.logoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.labelLarge,
                color = Neutral800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subscription.category.label,
                style = MaterialTheme.typography.bodySmall


            )

            val billingDate = subscription.nextBillingDate()
                ?.let { date -> formatDate(date, "MMMM d") }
                ?: subscription.renewalDate

            Text(
                text = "Renews on $billingDate",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatSubscriptionAmount(subscription.amount),
                style = MaterialTheme.typography.labelMedium,
                color = Primary900
            )

            Text(
                text = subscription.billingCycle,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
    }
}

/**
 * Shows press feedback immediately, including quick taps in a scrolling list.
 */
private fun Modifier.reportPressesTo(
    interactionSource: MutableInteractionSource
): Modifier = pointerInput(interactionSource) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val press = PressInteraction.Press(down.position)
        interactionSource.tryEmit(press)

        // Null means the touch was cancelled, such as when scrolling.
        val up = waitForUpOrCancellation()
        interactionSource.tryEmit(
            if (up != null) PressInteraction.Release(press) else PressInteraction.Cancel(press)
        )
    }
}

/**
 * Shows the selected logo, a matching app logo, or the first letter of the name.
 */
@Composable
internal fun SubscriptionLogo(
    name: String,
    modifier: Modifier = Modifier,
    logoUri: String? = null,
    size: Dp = 40.dp,
    cornerRadius: Dp = 10.dp
) {
    val logoModifier = modifier
        .size(size)
        .clip(RoundedCornerShape(cornerRadius))

    val customLogo = rememberCustomLogo(logoUri, size)
    val logoRes = logoFor(name)

    if (customLogo != null) {
        Image(
            bitmap = customLogo,
            contentDescription = "$name logo",
            modifier = logoModifier,
            contentScale = ContentScale.Crop
        )
    } else if (logoRes != null) {
        Image(
            painter = painterResource(
                id = logoRes
            ),
            contentDescription = "$name logo",
            modifier = logoModifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = logoModifier
                .background(Primary200),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Primary900
            )
        }
    }
}

/**
 * Loads the selected logo at its display size.
 * Returns null while loading or if the image cannot be read.
 */
@Composable
private fun rememberCustomLogo(
    logoUri: String?,
    size: Dp
): ImageBitmap? {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }

    return produceState<ImageBitmap?>(
        initialValue = null,
        logoUri,
        sizePx
    ) {
        value = if (logoUri == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                loadLogo(context, logoUri, sizePx)
            }
        }
    }.value
}

private fun loadLogo(
    context: Context,
    logoUri: String,
    targetPx: Int
): ImageBitmap? = try {
    val uri = logoUri.toUri()

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    }

    var sampleSize = 1
    while (
        bounds.outWidth / (sampleSize * 2) >= targetPx &&
        bounds.outHeight / (sampleSize * 2) >= targetPx
    ) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }?.asImageBitmap()
} catch (_: Exception) {
    null
}

@DrawableRes
internal fun logoFor(
    name: String
): Int? = when {
    name.contains(
        "netflix",
        ignoreCase = true
    ) -> R.drawable.logo_netflix

    name.contains(
        "spotify",
        ignoreCase = true
    ) -> R.drawable.logo_spotify

    name.contains(
        "youtube",
        ignoreCase = true
    ) -> R.drawable.logo_youtube

    name.contains(
        "adobe",
        ignoreCase = true
    ) -> R.drawable.logo_adobe

    name.contains(
        "canva",
        ignoreCase = true
    ) -> R.drawable.logo_canva

    name.contains(
        "google one",
        ignoreCase = true
    ) -> R.drawable.logo_google_one

    name.contains(
        "strava",
        ignoreCase = true
    ) -> R.drawable.logo_strava

    else -> null
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SubscriptionsScreenPreview() {
    SubybaraTheme {
        SubscriptionsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onSubscriptionClick = {},
            onDestinationClick = {},
            onAddSubscriptionClick = {}
        )
    }
}
