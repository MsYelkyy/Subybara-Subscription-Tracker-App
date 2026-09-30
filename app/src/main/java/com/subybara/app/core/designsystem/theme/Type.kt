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
    // Heading - 18 / 120% / SemiBold
    titleLarge = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 21.6.sp,
        letterSpacing = 0.sp
    ),

    // Section title - 16 / 120% / SemiBold
    titleMedium = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
        letterSpacing = 0.sp
    ),

    // Regular text - 14 / Regular
    bodyMedium = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),

    // Filter / small body - 12 / 140% / Regular
    bodySmall = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        letterSpacing = 0.sp
    ),

    // Shared button/action text
    labelLarge = TextStyle(
        fontFamily = GeologicaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
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