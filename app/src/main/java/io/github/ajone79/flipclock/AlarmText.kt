package io.github.ajone79.flipclock

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Short label for the next alarm: "07:30" if it rings within a day, else "Mon 07:30". */
object AlarmText {
    fun format(
        nowMillis: Long,
        alarmMillis: Long,
        is24Hour: Boolean,
        zone: ZoneId = ZoneId.systemDefault(),
        locale: Locale = Locale.getDefault(),
    ): String {
        val time = if (is24Hour) "HH:mm" else "h:mm a"
        val within24h = alarmMillis - nowMillis < 24L * 60 * 60 * 1000
        val pattern = if (within24h) time else "EEE $time"
        return DateTimeFormatter.ofPattern(pattern, locale)
            .format(Instant.ofEpochMilli(alarmMillis).atZone(zone))
    }
}
