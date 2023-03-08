package com.willbsp.habits.data.repository.local

import com.willbsp.habits.data.database.dao.HabitDao
import com.willbsp.habits.data.model.Habit
import com.willbsp.habits.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalHabitRepository @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getAllHabitsStream(): Flow<List<Habit>> {
        return habitDao.getAllHabitsStream()
    }

    override fun getHabitStream(habitId: Int): Flow<Habit?> {
        return habitDao.getHabitStream(habitId)
    }

    override suspend fun getHabit(habitId: Int): Habit? {
        return habitDao.getHabit(habitId)
    }

    override suspend fun addHabit(habit: Habit) {
        habitDao.insert(Habit(habit.id, habit.name, habit.frequency))
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.update(Habit(habit.id, habit.name, habit.frequency))
    }

    override suspend fun deleteHabit(habitId: Int) {
        val habit = habitDao.getHabit(habitId)
        if (habit != null) {
            habitDao.delete(habit)
        }
    }

}