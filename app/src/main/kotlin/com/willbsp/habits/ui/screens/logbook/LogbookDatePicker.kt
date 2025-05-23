package com.willbsp.habits.ui.screens.logbook

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue

private val MAX_WIDTH = 450.dp
private val MAX_HEIGHT = 350.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogbookDatePicker(
    modifier: Modifier = Modifier,
    logbookUiState: LogbookUiState.SelectedHabit,
    dateOnClick: (LocalDate) -> Unit,
    pages: Int = 1200,
) {

    val pagerState = rememberPagerState(
        initialPage = pages - 2
    ) { pages }

    VerticalPager(
        modifier = modifier,
        state = pagerState,
        pageSize = PageSize.Fixed(MAX_HEIGHT),
        userScrollEnabled = true,
        contentPadding = PaddingValues(top = MAX_HEIGHT / 2, bottom = MAX_HEIGHT / 2),
        horizontalAlignment = Alignment.CenterHorizontally,
        key = { it }
    ) { page ->
        val date = remember {
            logbookUiState.todaysDate.minusMonths((pages - page - 2).toLong())
        }
        LogbookMonth(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset =
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    alpha = lerp(
                        start = 0.25f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .fillMaxWidth(),
            date = date,
            page = page,
            logbookUiState = logbookUiState,
            dateOnClick = dateOnClick,
            pagerState = pagerState
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogbookMonth(
    modifier: Modifier = Modifier,
    date: LocalDate,
    page: Int,
    dateOnClick: (LocalDate) -> Unit,
    pagerState: PagerState,
    logbookUiState: LogbookUiState.SelectedHabit,
) {

    val startDate = remember { date.withDayOfMonth(1).with(DayOfWeek.MONDAY) }
    val scope = rememberCoroutineScope()
    val today = remember { logbookUiState.todaysDate }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        val height = minOf(maxHeight, MAX_HEIGHT)
        val width = minOf(maxWidth, MAX_WIDTH)

        Column(
            modifier = Modifier.width(width),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val monthText = remember {
                "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.year}"
            }

            Text(
                text = monthText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.width(width),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { row ->
                    val weekday = remember { startDate.plusDays(row.toLong()) }
                    Column(Modifier.height(height), verticalArrangement = Arrangement.SpaceEvenly) {
                        Text(
                            modifier = Modifier.width(40.dp),
                            text = weekday.dayOfWeek.getDisplayName(
                                TextStyle.NARROW,
                                Locale.getDefault()
                            ),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall
                        )
                        repeat(6) { col ->
                            val currentDate = remember { weekday.plusWeeks(col.toLong()) }
                            if (currentDate.month == date.month) {
                                DateIconButton(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag(currentDate.toString()),
                                    date = currentDate,
                                    checked = logbookUiState.completed.contains(currentDate),
                                    checkedSecondary = logbookUiState.completedByWeek.contains(
                                        currentDate
                                    ),
                                    enabled = !currentDate.isAfter(today),
                                    onCheckedChange = { dateOnClick(currentDate) }
                                )
                            } else {
                                Box(Modifier.size(40.dp))
                            }
                        }
                    }
                }
            }
        }

    }

}

@Composable
private fun DateIconButton(
    modifier: Modifier = Modifier,
    date: LocalDate,
    checked: Boolean,
    checkedSecondary: Boolean,
    enabled: Boolean,
    onCheckedChange: (LocalDate) -> (Unit),
) {

    val dayOfMonth = remember { date.dayOfMonth.toString() }
    val colors = if (checkedSecondary) {
        IconButtonDefaults.filledIconToggleButtonColors(
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    } else {
        IconButtonDefaults.filledIconToggleButtonColors()
    }

    AnimatedContent(
        targetState = Pair(checked, colors),
        label = "DateIconButton"
    ) { (checked, colors) ->
        FilledIconToggleButton(
            modifier = modifier,
            checked = checked,
            enabled = enabled,
            onCheckedChange = { onCheckedChange(date) },
            colors = colors
        ) {
            Text(
                text = dayOfMonth,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }

}

/*@Preview(showBackground = true)
@Composable
private fun NewLogbookDatePickerPreview() {
    LogbookDatePicker(
        modifier = Modifier.fillMaxSize(),
        logbookUiState = listOf(
            LocalDate.parse("2023-03-22"),
            LocalDate.parse("2023-03-23"),
            LocalDate.parse("2023-03-24")
        ),
        dateOnClick = { }
    )
}*/