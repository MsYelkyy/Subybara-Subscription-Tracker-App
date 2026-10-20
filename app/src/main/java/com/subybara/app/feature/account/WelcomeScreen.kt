package com.subybara.app.feature.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraTextAction
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.SubybaraTheme

/**
 * First screen shown when the app opens.
 *
 * Navigation is handled by the caller through the click callbacks, so this
 * composable only describes what the screen looks like.
 *
 * @param onGetStartedClick Called when the user taps "Get Started".
 * @param onSignInClick Called when the user taps "I already have an account".
 * @param modifier Modifier applied to the root layout.
 */
@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isLandscape = maxWidth > maxHeight
        val designScale = if (isLandscape) {
            minOf(maxWidth.value / WelcomeDimens.BaseWidth, 1f)
        } else {
            minOf(
                maxWidth.value / WelcomeDimens.BaseWidth,
                maxHeight.value / WelcomeDimens.BaseHeight
            )
        }
        val contentHeight = maxOf(maxHeight, WelcomeDimens.BaseHeight.scaledDp(designScale))
        val screenWidth = maxWidth

        val horizontalPadding =
            WelcomeDimens.HorizontalPadding.scaledDp(designScale)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .height(contentHeight)
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.bg_welcome
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(
                    id = R.drawable.capy_welcome
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = WelcomeDimens.CapyTop.scaledDp(designScale)
                    )
                    .size(
                        WelcomeDimens.CapySize.scaledDp(designScale)
                    ),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(
                    id = R.drawable.logo_subybara
                ),
                contentDescription = "Subybara",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = WelcomeDimens.LogoTop.scaledDp(designScale)
                    )
                    .width(
                        minOf(
                            WelcomeDimens.LogoWidth * designScale,
                            screenWidth.value
                        ).dp
                    )
                    .aspectRatio(
                        WelcomeDimens.LogoAspectRatio
                    ),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Track your subscriptions.\nAvoid surprise charges.",
                color = Neutral800,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = WelcomeDimens.CopyTop.scaledDp(designScale)
                    )
                    .padding(
                        horizontal = horizontalPadding
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.sp
                )
            )

            SubybaraPrimaryButton(
                text = "Get Started",
                onClick = onGetStartedClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = WelcomeDimens.ButtonTop.scaledDp(designScale)
                    )
                    .padding(
                        horizontal = horizontalPadding
                    )
                    .fillMaxWidth()
                    .height(
                        WelcomeDimens.ButtonHeight.scaledDp(designScale)
                    ),
                shape = RoundedCornerShape(
                    WelcomeDimens.ButtonRadius.scaledDp(designScale)
                )
            )

            SubybaraTextAction(
                text = "I already have an account",
                onClick = onSignInClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = WelcomeDimens.SignInTop.scaledDp(designScale)
                    ),
                color = Primary600
            )
        }
    }
}

/**
 * Figma measurements for the Welcome screen,
 * based on a 375 x 800 frame.
 */
private object WelcomeDimens {
    const val BaseWidth = 375f
    const val BaseHeight = 800f

    const val HorizontalPadding = 24f

    const val CapyTop = 71f
    const val CapySize = 336f

    const val LogoTop = 302f
    const val LogoWidth = 352f
    const val LogoAspectRatio = 1.5f

    const val CopyTop = 455f

    const val ButtonTop = 586f
    const val ButtonHeight = 44f
    const val ButtonRadius = 18f

    const val SignInTop = 650f
}

private fun Float.scaledDp(
    scale: Float
): Dp = (this * scale).dp

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun WelcomeScreenPreview() {
    SubybaraTheme {
        WelcomeScreen(
            onGetStartedClick = {},
            onSignInClick = {}
        )
    }
}
