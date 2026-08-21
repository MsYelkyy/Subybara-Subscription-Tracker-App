package com.subybara.app.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.subybara.app.R

val GeologicaFontFamily = FontFamily(
    geologicaFont(weight = FontWeight.Normal, weightAxis = 400),
    geologicaFont(weight = FontWeight.Medium, weightAxis = 500),
    geologicaFont(weight = FontWeight.SemiBold, weightAxis = 600),
    geologicaFont(weight = FontWeight.Bold, weightAxis = 700)
)

@OptIn(ExperimentalTextApi::class)
private fun geologicaFont(
    weight: FontWeight,
    weightAxis: Int
): Font = Font(
    resId = R.font.geologica_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weightAxis))
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
)
