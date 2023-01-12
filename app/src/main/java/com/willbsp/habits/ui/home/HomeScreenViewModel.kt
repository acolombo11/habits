package com.willbsp.habits.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willbsp.habits.data.model.Entry
import com.willbsp.habits.data.repo.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class HomeUiState(
    val state: List<HomeHabitUiState> = listOf()
)

data class HomeHabitUiState(
    val id: Int,
    val name: String,
    val completed: Boolean
)

class HomeScreenViewModel(private val habitsRepository: HabitRepository) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> =
        habitsRepository.getAllHabitsStream().flatMapLatest { habitList ->
            val homeHabitUiStateFlows: List<Flow<HomeHabitUiState>> = habitList.map { habit ->
                habitsRepository.entryExistsForDateStream(getCurrentDate(), habit.id)
                    .map { exists ->
                        HomeHabitUiState(habit.id, habit.name, exists)
                    }
            }
            combine(homeHabitUiStateFlows) { homeHabitUiStateArray ->
                HomeUiState(homeHabitUiStateArray.toList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    suspend fun toggleEntry(habitId: Int) {
        val date: String = getCurrentDate()
        val entry: Entry? = habitsRepository.getEntryForDate(date, habitId)
        if (entry == null) {
            habitsRepository.insertEntry(
                Entry(
                    habitId = habitId,
                    date = date
                )
            )
        } else {
            habitsRepository.deleteEntry(entry)
        }
    }

    private fun getCurrentDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(formatter)
    }

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

}