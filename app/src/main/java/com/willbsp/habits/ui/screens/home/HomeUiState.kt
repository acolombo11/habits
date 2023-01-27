package com.willbsp.habits.ui.screens.home

import java.time.LocalDate

data class HomeUiState(
    val todayState: List<HomeHabitUiState> = listOf()
)

data class HomeHabitUiState(
    val id: Int,
    val name: String,
    val completedDates: List<HomeCompletedUiState>
)

data class HomeCompletedUiState(
    val date: LocalDate,
    val completed: Boolean
)