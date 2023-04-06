package com.willbsp.habits.usecases

import com.willbsp.habits.TestData.habit3
import com.willbsp.habits.TestData.habit4
import com.willbsp.habits.domain.model.Streak
import com.willbsp.habits.domain.usecase.CalculateStreakUseCase
import com.willbsp.habits.domain.usecase.GetVirtualEntriesUseCase
import com.willbsp.habits.fake.repository.FakeEntryRepository
import com.willbsp.habits.fake.repository.FakeHabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalculateStreakUseCaseTest {

    private val date = LocalDate.parse("2023-03-10")

    // private val time = "T12:00:00Z"
    private lateinit var entryRepository: FakeEntryRepository
    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var getVirtualEntriesUseCase: GetVirtualEntriesUseCase
    private lateinit var calculateStreakUseCase: CalculateStreakUseCase

    @Before
    fun setup() {
        // val clock = Clock.fixed(Instant.parse(date.toString() + time), ZoneOffset.UTC)
        entryRepository = FakeEntryRepository()
        habitRepository = FakeHabitRepository()
        getVirtualEntriesUseCase = GetVirtualEntriesUseCase(habitRepository, entryRepository)
        calculateStreakUseCase = CalculateStreakUseCase(getVirtualEntriesUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenStreak_streakCalculated() = runTest {
        val correctStreak = 5
        habitRepository.upsertHabit(habit3)
        entryRepository.populate()
        val streak = calculateStreakUseCase(habit3.id).first()
        assertEquals(correctStreak, streak.first().length)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenMultipleStreaks_streaksCalculated() = runTest {
        val correctStreaks = listOf(
            Streak(5, LocalDate.parse("2023-03-09")),
            Streak(3, LocalDate.parse("2023-03-03"))
        )
        habitRepository.upsertHabit(habit3)
        entryRepository.populate()
        entryRepository.toggleEntry(habit3.id, LocalDate.parse("2023-03-02"))
        val streaks = calculateStreakUseCase(habit3.id)
        assertEquals(correctStreaks, streaks.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenWeeklyHabitCompleted_streaksCalculated() = runTest {
        val correctStreaks = listOf(
            Streak(7, LocalDate.parse("2023-03-05")),
        )
        habitRepository.upsertHabit(habit4)
        entryRepository.populate2()
        val streaks = calculateStreakUseCase(habit4.id)
        assertEquals(correctStreaks, streaks.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenWeeklyHabitUncompleted_streaksCalculated() = runTest {
        val correctStreaks = listOf(
            Streak(3, LocalDate.parse("2023-03-02")),
        )
        habitRepository.upsertHabit(habit4.copy(repeat = 4))
        entryRepository.populate2()
        val streaks = calculateStreakUseCase(habit4.id)
        assertEquals(correctStreaks, streaks.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenNoStreak_returnEmptyList() = runTest {
        habitRepository.upsertHabit(habit3)
        entryRepository.toggleEntry(habit3.id, date.minusDays(3f.toLong()))
        entryRepository.toggleEntry(habit3.id, date.minusDays(6f.toLong()))
        entryRepository.toggleEntry(habit3.id, date.minusDays(8f.toLong()))
        assertTrue(calculateStreakUseCase(habit3.id).first().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calculateStreak_whenEmptyList_returnEmptyList() = runTest {
        assertTrue(calculateStreakUseCase(habit3.id).first().isEmpty())
    }

}