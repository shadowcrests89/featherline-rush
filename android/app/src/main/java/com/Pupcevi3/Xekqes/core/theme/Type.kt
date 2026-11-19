package com.Pupcevi3.Xekqes.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontSize = 40.sp,
        lineHeight = 46.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.5.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 26.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.6.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 15.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 3.sp
    )
)

val ScoreStyle = TextStyle(
    fontSize = 30.sp,
    lineHeight = 34.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 1.sp,
    fontFeatureSettings = "tnum"
)

val BigScoreStyle = TextStyle(
    fontSize = 44.sp,
    lineHeight = 50.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 1.5.sp,
    fontFeatureSettings = "tnum"
)

val StatValueStyle = TextStyle(
    fontSize = 22.sp,
    lineHeight = 26.sp,
    fontWeight = FontWeight.Bold,
    fontFeatureSettings = "tnum"
)
