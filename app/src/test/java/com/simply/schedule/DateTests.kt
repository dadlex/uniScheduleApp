package com.simply.schedule

import com.simply.schedule.ScheduleDbHelper.Companion.getShortDate
import com.simply.schedule.ScheduleDbHelper.Companion.getSimpleDate
import org.joda.time.LocalDate
import org.junit.Test

import org.junit.Assert.*

class DateTest {
    @Test
    fun testDateFormatting() {
        assertEquals(
            "01.01.20",
            getShortDate(LocalDate(2020, 1, 1))
        )
        assertEquals(
            "1 January",
            getSimpleDate(LocalDate(2020, 1, 1))
        )
        assertEquals(
            "1 January 2010",
            getSimpleDate(LocalDate(2010, 1, 1))
        )
    }
}
