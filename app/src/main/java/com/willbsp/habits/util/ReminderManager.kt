package com.willbsp.habits.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.willbsp.habits.data.repository.HabitRepository
import com.willbsp.habits.data.repository.ReminderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ReminderManager @Inject constructor(
    @ApplicationContext val context: Context,
    val habitRepository: HabitRepository,
    val reminderRepository: ReminderRepository
) {

    suspend fun scheduleReminder(reminderId: Int, day: DayOfWeek, time: LocalTime) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }

        val reminder = reminderRepository.getReminderStream(reminderId).first()
        val habit = habitRepository.getHabit(reminder.habitId)

        val intent = Intent(context.applicationContext, ReminderReceiver::class.java)
        intent.putExtra("reminderId", reminderId)
        if (habit != null) {
            intent.putExtra("habitName", habit.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext, reminderId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminderTime: Calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.DAY_OF_WEEK, day.toCalendarDay())
        }

        // if date is in the past, schedule for next week
        if (Calendar.getInstance(Locale.getDefault())
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - reminderTime.timeInMillis > 0
        ) {
            reminderTime.add(Calendar.WEEK_OF_YEAR, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(reminderTime.timeInMillis, pendingIntent), pendingIntent
        )

    }

    fun unscheduleReminder(reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.cancel(intent)
    }

    private fun DayOfWeek.toCalendarDay(): Int {
        return (value % 7) + 1
    }

}