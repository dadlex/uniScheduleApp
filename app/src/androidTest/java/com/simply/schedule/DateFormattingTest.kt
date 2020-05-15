package com.simply.schedule

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.simply.schedule.ScheduleDbHelper.Companion.getDateRelativeToToday
import org.joda.time.LocalDate

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DateFormattingTest {
    @Test
    fun testGetDateRelativeToToday() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(
            appContext.getString(R.string.today),
            getDateRelativeToToday(appContext, LocalDate.now())
        )
        assertEquals(
            appContext.getString(R.string.tomorrow),
            getDateRelativeToToday(appContext, LocalDate.now().plusDays(1))
        )
        assertEquals(
            appContext.getString(R.string.yesterday),
            getDateRelativeToToday(appContext, LocalDate.now().minusDays(1))
        )
        assertEquals(
            LocalDate.now().plusDays(2).toString("EEEE"),
            getDateRelativeToToday(appContext, LocalDate.now().plusDays(2))
        )
        assertEquals(
            appContext.getString(R.string.next_month),
            getDateRelativeToToday(appContext, LocalDate.now().plusMonths(1).plusDays(7))
        )
        assertEquals(
            appContext.getString(R.string.last_month),
            getDateRelativeToToday(appContext, LocalDate.now().minusMonths(1).minusDays(7))
        )
        assertEquals(
            LocalDate.now().minusMonths(2).toString("MMMM").capitalize(),
            getDateRelativeToToday(appContext, LocalDate.now().minusMonths(2)).capitalize()
        )
    }
}
