package com.subybara.app.feature.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.subybara.app.core.designsystem.theme.SubybaraBrown
import com.subybara.app.core.designsystem.theme.SubybaraGreen
import com.subybara.app.core.designsystem.theme.SubybaraTheme

@Composable
fun WelcomeScreen(
    state: WelcomeUiState,
    onAction: (WelcomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val designScale = minOf(
            maxWidth.value / WelcomeDesignTokens.BaseWidth,
            maxHeight.value / WelcomeDesignTokens.BaseHeight
        )
        val horizontalPadding = WelcomeDesignTokens.HorizontalPadding.scaledDp(designScale)

        Image(
            painter = painterResource(id = R.drawable.bg_welcome),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.capy_welcome),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = WelcomeDesignTokens.CapyTop.scaledDp(designScale))
                .size(WelcomeDesignTokens.CapySize.scaledDp(designScale)),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = state.title,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = WelcomeDesignTokens.LogoTop.scaledDp(designScale))
                .width(minOf(WelcomeDesignTokens.LogoWidth * designScale, maxWidth.value).dp)
                .aspectRatio(WelcomeDesignTokens.LogoAspectRatio),
            contentScale = ContentScale.Fit
        )

        Text(
            text = state.headline,
            color = SubybaraBrown,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = WelcomeDesignTokens.CopyTop.scaledDp(designScale))
                .padding(horizontal = horizontalPadding)
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
            text = state.primaryActionLabel,
            onClick = { onAction(WelcomeAction.GetStarted) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = WelcomeDesignTokens.ButtonTop.scaledDp(designScale))
                .padding(horizontal = horizontalPadding)
                .fillMaxWidth()
                .height(WelcomeDesignTokens.ButtonHeight.scaledDp(designScale)),
            shape = RoundedCornerShape(WelcomeDesignTokens.ButtonRadius.scaledDp(designScale))
        )

        SubybaraTextAction(
            text = state.secondaryActionLabel,
            onClick = { onAction(WelcomeAction.SignIn) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = WelcomeDesignTokens.SignInTop.scaledDp(designScale)),
            color = SubybaraGreen,
            shape = RoundedCornerShape(WelcomeDesignTokens.SignInRadius.dp)
        )
    }
}

private fun Float.scaledDp(scale: Float): Dp = (this * scale).dp

@Preview(showBackground = true, widthDp = 375, heightDp = 800)
@Composable
private fun WelcomeScreenPreview() {
    SubybaraTheme {
        WelcomeScreen(
            state = WelcomeUiState(),
            onAction = {}
        )
    }
}
