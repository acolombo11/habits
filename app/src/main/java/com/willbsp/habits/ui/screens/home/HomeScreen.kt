package com.willbsp.habits.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.willbsp.habits.R
import com.willbsp.habits.data.model.HabitFrequency
import com.willbsp.habits.ui.common.FullscreenHint
import com.willbsp.habits.ui.common.button.HabitsFloatingAction
import com.willbsp.habits.ui.theme.HabitsTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    completedOnClick: (Int, LocalDate) -> Unit,
    navigateToLogbook: () -> Unit,
    navigateToAddHabit: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    navigateToSettings: () -> Unit,
    homeUiState: HomeUiState
) {

    var showCompleted by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = navigateToLogbook) {
                        Icon(
                            imageVector = Icons.TwoTone.DateRange,
                            contentDescription = stringResource(R.string.home_logbook)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showCompleted = !showCompleted
                    }) {
                        Icon(
                            imageVector = if (showCompleted) Icons.TwoTone.Visibility else Icons.TwoTone.VisibilityOff,
                            contentDescription = stringResource(R.string.home_show_completed)
                        )
                    }
                    IconButton(onClick = navigateToSettings) {
                        Icon(
                            imageVector = Icons.TwoTone.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            HabitsFloatingAction(
                onClick = navigateToAddHabit,
                icon = Icons.TwoTone.Add,
                contentDescription = stringResource(R.string.home_add_habit)
            )
        }
    ) { innerPadding ->

        when (homeUiState) {
            is HomeUiState.Empty -> {
                FullscreenHint(
                    modifier = Modifier.fillMaxSize(),
                    icon = Icons.TwoTone.Add,
                    iconContentDescription = R.string.home_all_completed_tick,
                    text = R.string.home_no_habits
                )
            }

            is HomeUiState.Habits -> {

                val allCompleted = remember(homeUiState) {
                    homeUiState.habits.all { it.hasBeenCompleted(homeUiState.todaysDate) }
                }
                val showHabits = !showCompleted && allCompleted

                AnimatedVisibility(
                    visible = showHabits,
                    enter = scaleIn(TweenSpec(delay = 400)),
                    exit = scaleOut()
                ) {
                    FullscreenHint(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.TwoTone.Done,
                        iconContentDescription = R.string.home_all_completed_tick,
                        text = R.string.home_all_completed
                    )
                }

                AnimatedVisibility(
                    visible = !showHabits,
                    enter = fadeIn(),
                    exit = fadeOut(TweenSpec(delay = 200))
                ) {
                    HomeHabitList(
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        homeUiState = homeUiState,
                        completedOnClick = completedOnClick,
                        navigateToDetail = navigateToDetail,
                        showCompleted = showCompleted
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenNoHabitsPreview() {
    HabitsTheme {
        HomeScreen(
            snackbarHostState = SnackbarHostState(),
            navigateToAddHabit = {},
            navigateToDetail = {},
            navigateToSettings = {},
            navigateToLogbook = {},
            homeUiState = HomeUiState.Empty,
            completedOnClick = { _, _ -> },
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenHabitsPreview() {
    val habits = listOf(
        HomeUiState.Habit(0, "Running", HabitFrequency.DAILY, 3, 0, listOf(), listOf()),
        HomeUiState.Habit(
            id = 1,
            name = "Boxing",
            type = HabitFrequency.WEEKLY,
            streak = 2,
            score = 0,
            completed = listOf(LocalDate.parse("2023-07-08")),
            completedByWeek = listOf()
        )
    )
    HabitsTheme {
        HomeScreen(
            snackbarHostState = SnackbarHostState(),
            navigateToAddHabit = {},
            navigateToDetail = {},
            navigateToSettings = {},
            navigateToLogbook = {},
            homeUiState = HomeUiState.Habits(
                habits = habits,
                showStreaks = true,
                showSubtitle = true,
                showScore = false,
                todaysDate = LocalDate.of(2023, 7, 8)
            ),
            completedOnClick = { _, _ -> },
        )
    }
}
