package com.willbsp.habits.ui.screens.logbook

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.willbsp.habits.ui.theme.Typography
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogbookDatePicker(
    modifier: Modifier = Modifier,
    logbookUiState: LogbookUiState.SelectedHabit,
    dateOnClick: (LocalDate) -> Unit
) {

    val pagerState = rememberPagerState(Integer.MAX_VALUE)
    VerticalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = false,
        horizontalAlignment = Alignment.CenterHorizontally,
        pageCount = Integer.MAX_VALUE,
        key = { it }
    ) {
        val date = remember { LocalDate.now().minusMonths((Integer.MAX_VALUE - it - 1).toLong()) }
        LogbookMonth(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset =
                        ((pagerState.currentPage - it) + pagerState.currentPageOffsetFraction).absoluteValue
                    val fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    alpha = lerp(start = 0f, stop = 1f, fraction = fraction)
                },
            date = date,
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
    dateOnClick: (LocalDate) -> Unit,
    pagerState: PagerState,
    logbookUiState: LogbookUiState.SelectedHabit
) {

    val startDate = remember { date.withDayOfMonth(1).with(DayOfWeek.MONDAY) }
    val scope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }

    Column(modifier.width(350.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        val monthText = remember {
            "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.year}"
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = null
                )
            }
            Spacer(modifier.weight(1f))
            Text(
                text = monthText,
                style = Typography.headlineLarge
            )
            Spacer(modifier.weight(1f))
            IconButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(7) { row ->
                val weekday = remember { startDate.plusDays(row.toLong()) }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        modifier = Modifier.width(40.dp),
                        text = weekday.dayOfWeek.getDisplayName(
                            TextStyle.NARROW,
                            Locale.getDefault()
                        ),
                        textAlign = TextAlign.Center
                    )
                    repeat(6) { col ->
                        val currentDate = remember { weekday.plusWeeks(col.toLong()) }
                        if (currentDate.month == date.month) {
                            DateIconButton(
                                modifier = Modifier.size(40.dp),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DateIconButton(
    modifier: Modifier = Modifier,
    date: LocalDate,
    checked: Boolean,
    checkedSecondary: Boolean,
    enabled: Boolean,
    onCheckedChange: (LocalDate) -> (Unit)
) {

    val dayOfMonth = remember { date.dayOfMonth.toString() }
    val colors = if (checkedSecondary) IconButtonDefaults.filledIconToggleButtonColors(
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) else IconButtonDefaults.filledIconToggleButtonColors()

    AnimatedContent(targetState = checked) {
        FilledIconToggleButton(
            modifier = modifier,
            checked = checked,
            enabled = enabled,
            onCheckedChange = {
                onCheckedChange(date)
            },
            colors = colors
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(dayOfMonth, style = Typography.bodyLarge)
            }
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