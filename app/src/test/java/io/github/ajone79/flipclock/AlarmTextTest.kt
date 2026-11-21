package io.github.ajone79.flipclock

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class AlarmTextTest {
    private val zone = ZoneId.of("Europe/London")
    private fun at(day: Int, h: Int, m: Int) =
        ZonedDateTime.of(2026, 11, day, h, m, 0, 0, zone).toInstant().toEpochMilli()

    @Test fun sameDayShowsTimeOnly24h() {
        assertEquals("07:30", AlarmText.format(at(21, 22, 0), at(22, 7, 30), true, zone, Locale.UK))
    }

    @Test fun laterShowsWeekday24h() {
        // 21 Nov 2026 is a Saturday; 23 Nov is a Monday
        assertEquals("Mon 07:30", AlarmText.format(at(21, 22, 0), at(23, 7, 30), true, zone, Locale.UK))
    }

    @Test fun twelveHourFormat() {
        assertEquals("7:05 PM", AlarmText.format(at(21, 9, 0), at(21, 19, 5), false, zone, Locale.US))
    }
}
