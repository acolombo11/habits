package com.willbsp.habits.ui.screens.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willbsp.habits.data.repository.HabitRepository
import com.willbsp.habits.data.repository.ReminderRepository
import com.willbsp.habits.domain.usecase.ValidateHabitNameUseCase
import com.willbsp.habits.ui.common.HabitReminderTypes
import com.willbsp.habits.ui.common.HabitUiState
import com.willbsp.habits.ui.common.toHabit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val reminderRepository: ReminderRepository,
    private val isValidHabitName: ValidateHabitNameUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState: HabitUiState by mutableStateOf(HabitUiState.Loading)
        private set

    private var habitId: Int = checkNotNull(savedStateHandle[HABIT_ID_SAVED_STATE_KEY])

    init {
        loadHabit()
    }

    fun updateUiState(newUiState: HabitUiState.Habit) {
        uiState = if (isValidHabitName(newUiState.name)) {
            newUiState.copy(nameIsInvalid = false)
        } else newUiState.copy(nameIsInvalid = true)
    }

    fun deleteHabit() {
        viewModelScope.launch { habitRepository.deleteHabit(habitId) }
    }

    fun saveHabit(): Boolean {
        when (uiState) {
            is HabitUiState.Habit -> {
                val habitState = uiState as HabitUiState.Habit
                if (isValidHabitName(habitState.name)) {
                    viewModelScope.launch { habitRepository.upsertHabit(habitState.toHabit(habitId)) }
                    return true
                }
                return false
            }

            else -> return false
        }
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = habitRepository.getHabit(habitId)
            if (habit != null) {
                val reminders = reminderRepository.getRemindersForHabitStream(habitId).first()
                uiState = HabitUiState.Habit(
                    name = habit.name,
                    frequency = habit.frequency,
                    repeat = habit.repeat,
                    reminderType = getReminderType(reminders.count()),
                    reminderTime = reminders.first().time,
                    reminderDays = reminders.map { it.day }
                )
            }
        }
    }

    private fun getReminderType(reminderCount: Int): HabitReminderTypes {
        return when (reminderCount) {
            7 -> HabitReminderTypes.EVERYDAY
            0 -> HabitReminderTypes.NONE
            else -> HabitReminderTypes.SPECIFIC
        }
    }

    companion object {
        private const val HABIT_ID_SAVED_STATE_KEY = "habitId"
    }

}