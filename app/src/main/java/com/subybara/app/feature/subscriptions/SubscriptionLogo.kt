package com.subybara.app.feature.subscriptions

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Primary200
import com.subybara.app.core.designsystem.theme.Primary900
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    name.contains(
        "disney",
        ignoreCase = true
    ) -> R.drawable.logo_disney_plus

    else -> null
}
