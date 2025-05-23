package com.willbsp.habits.ui.screens.logbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.SentimentVeryDissatisfied
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.willbsp.habits.R
import com.willbsp.habits.ui.common.DefaultHabitsAppTopBar
import com.willbsp.habits.ui.common.FullscreenHint
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    logbookUiState: LogbookUiState,
    completedOnClick: (LocalDate) -> Unit,
    habitOnClick: (Int) -> Unit,
) {

    Scaffold(
        topBar = {
            DefaultHabitsAppTopBar(
                title = stringResource(R.string.logbook_title),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->

        when (logbookUiState) {

            is LogbookUiState.SelectedHabit -> {
                LogbookDatePicker(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    logbookUiState = logbookUiState,
                    dateOnClick = completedOnClick
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider()

                    LazyRow(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 10.dp,
                            bottom = 10.dp,
                            end = 8.dp
                        )
                    ) {
                        items(items = logbookUiState.habits, key = { it.id }) {
                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                selected = it.id == logbookUiState.habitId,
                                onClick = { habitOnClick(it.id) },
                                label = {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    }

                }

            }

            is LogbookUiState.NoHabits -> {
                FullscreenHint(
                    modifier = modifier.fillMaxSize(),
                    icon = Icons.TwoTone.SentimentVeryDissatisfied,
                    iconContentDescription = R.string.logbook_add_a_new_habit,
                    text = R.string.logbook_empty_text
                )
            }

        }
    }
}

@Preview
@Composable
fun LogbookPreview() {
    LogbookScreen(
        navigateUp = {},
        logbookUiState = LogbookUiState.SelectedHabit(
            habitId = 1,
            todaysDate = LocalDate.of(2023, 6, 27),
            listOf(LocalDate.of(2023, 6, 23), LocalDate.of(2023, 6, 20)),
            listOf(),
            listOf(LogbookUiState.Habit(3, "gaming"), LogbookUiState.Habit(4, "epic"))
        ),
        completedOnClick = { },
        habitOnClick = { }
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun LogbookPreviewSmall() {
    LogbookScreen(
        navigateUp = {},
        logbookUiState = LogbookUiState.SelectedHabit(
            habitId = 1,
            todaysDate = LocalDate.of(2023, 6, 27),
            listOf(LocalDate.of(2023, 6, 23), LocalDate.of(2023, 6, 20)),
            listOf(),
            listOf(LogbookUiState.Habit(3, "gaming"), LogbookUiState.Habit(4, "epic"))
        ),
        completedOnClick = { },
        habitOnClick = { }
    )
}
