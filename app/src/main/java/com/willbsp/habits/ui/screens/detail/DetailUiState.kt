package com.willbsp.habits.ui.screens.detail

import java.time.LocalDate

data class DetailUiState(
    val habitId: Int,
    val habitName: String = "",
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val started: LocalDate = LocalDate.now(),
    val totalDays: Int = 0,
    val score: Int = 0,
)
