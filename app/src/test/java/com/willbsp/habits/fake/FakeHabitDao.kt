package com.willbsp.habits.fake

import com.willbsp.habits.data.database.dao.HabitDao
import com.willbsp.habits.data.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHabitDao : HabitDao {

    val habits = mutableListOf<Habit>()
    private val observableHabits = MutableStateFlow<List<Habit>>(listOf())
    private suspend fun emit() = observableHabits.emit(habits.toList())

    override fun getAllHabitsStream(): Flow<List<Habit>> = observableHabits

    override fun getHabitStream(id: Int): Flow<Habit?> =
        observableHabits.map { it.find { habit -> habit.id == id } }

    override suspend fun getHabit(id: Int): Habit? = habits.find { it.id == id }

    override suspend fun insert(habit: Habit) {
        habits.add(habit)
        emit()
    }

    override suspend fun update(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) habits[index] = habit
        emit()
    }

    override suspend fun delete(habit: Habit) {
        habits.removeAll { it == habit }
        emit()
    }

}