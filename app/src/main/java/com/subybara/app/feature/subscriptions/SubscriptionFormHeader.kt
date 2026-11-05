package com.subybara.app.feature.subscriptions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary300

@Composable
internal fun FormTopBar(
    title: String,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 12.dp, end = 10.dp)
    ) {
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
                tint = Color.Unspecified
            )
        }

        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            color = Neutral800
        )
    }
}

/**
 * Keeps read access to a picked logo after the screen that picked it closes.
 * Without this, the logo can no longer be loaded once the user leaves the app.
 * Pickers that do not offer lasting access are ignored.
 */
internal fun keepLogoAccess(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: SecurityException) {
    }
}

@Composable
internal fun FormHeader(
    @DrawableRes headerImage: Int,
    name: String,
    logoUri: String?,
    onLogoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoTile(
            name = name,
            logoUri = logoUri,
            onClick = onLogoClick
        )

        Spacer(modifier = Modifier.width(28.dp))

        Image(
            painter = painterResource(headerImage),
            contentDescription = null,
            modifier = Modifier.height(108.dp)
        )
    }
}

@Composable
private fun LogoTile(
    name: String,
    logoUri: String?,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val hasLogo = logoUri != null || logoFor(name) != null

    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 70.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 0.dp,
                    spread = 0.dp,
                    color = Primary300,
                    offset = DpOffset(0.dp, 1.dp)
                )
            )
            .background(MaterialTheme.colorScheme.background, shape)
            .border(1.dp, CardBorder, shape)
            .clip(shape)
            .clickable(
                onClickLabel = "Choose logo",
                role = Role.Button,
                onClick = onClick
            )
    ) {
        if (hasLogo) {
            SubscriptionLogo(
                name = name,
                modifier = Modifier.align(Alignment.Center),
                logoUri = logoUri,
                size = 68.dp,
                cornerRadius = 8.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Add Logo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    ),
                    color = Neutral500
                )
            }
        }
    }
}
