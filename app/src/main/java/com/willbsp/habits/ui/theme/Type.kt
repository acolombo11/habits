package com.willbsp.habits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.willbsp.habits.R

val Typography
    @Composable
    get() = MaterialTheme.typography.copy(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontFamily = Fonts.Unbounded,
            fontSize = 48.sp, // Avoid timePicker selected time overflow
        ),
        displayMedium = MaterialTheme.typography.displayMedium.copy(
            fontFamily = Fonts.Unbounded
        ),
        displaySmall = MaterialTheme.typography.displaySmall.copy(
            fontFamily = Fonts.Unbounded
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontFamily = Fonts.Unbounded
        ),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = Fonts.Unbounded
        ),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = Fonts.Unbounded
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontFamily = Fonts.Unbounded,
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontFamily = Fonts.Unbounded,
        ),
        titleSmall = MaterialTheme.typography.titleSmall.copy(
            fontFamily = Fonts.Unbounded,
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = Fonts.WorkSans,
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = Fonts.WorkSans,
        ),
        bodySmall = MaterialTheme.typography.bodySmall.copy(
            fontFamily = Fonts.WorkSans,
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontFamily = Fonts.Unbounded,
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontFamily = Fonts.Unbounded,
        ),
        labelSmall = MaterialTheme.typography.labelSmall.copy(
            fontFamily = Fonts.Unbounded,
        ),
    )

object Fonts {
    val Unbounded = FontFamily(
        Font(R.font.unbounded_regular, FontWeight.Normal),
        Font(R.font.unbounded_medium, FontWeight.Medium),
        Font(R.font.unbounded_semi_bold, FontWeight.SemiBold),
    )
    val WorkSans = FontFamily(
        Font(R.font.work_sans_regular, FontWeight.Normal),
        Font(R.font.work_sans_semi_bold, FontWeight.SemiBold),
    )
}



