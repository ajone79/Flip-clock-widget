package io.github.ajone79.flipclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.text.format.DateFormat
import android.view.View
import android.widget.RemoteViews

/**
 * The digits and date are TextClocks, so Android ticks them every minute by itself:
 * no background work, no battery cost. This class only wires up taps, the AM/PM
 * label and the next-alarm text, and refreshes when the alarm, time or locale changes.
 */
class FlipClockWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val views = build(context)
        ids.forEach { manager.updateAppWidget(it, views) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED -> refreshAll(context)
        }
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, FlipClockWidget::class.java))
            if (ids.isEmpty()) return
            val views = build(context)
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun build(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_flip_clock)
            val is24 = DateFormat.is24HourFormat(context)

            views.setViewVisibility(R.id.ampm, if (is24) View.GONE else View.VISIBLE)

            val alarm = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).nextAlarmClock
            if (alarm != null) {
                views.setTextViewText(
                    R.id.alarm_text,
                    AlarmText.format(System.currentTimeMillis(), alarm.triggerTime, is24),
                )
                views.setViewVisibility(R.id.alarm_icon, View.VISIBLE)
                views.setViewVisibility(R.id.alarm_text, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.alarm_icon, View.GONE)
                views.setViewVisibility(R.id.alarm_text, View.GONE)
            }

            val clockTap = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            views.setOnClickPendingIntent(R.id.clock_area, activity(context, 1, clockTap))

            val calendarUri = CalendarContract.CONTENT_URI.buildUpon()
                .appendPath("time")
                .appendPath(System.currentTimeMillis().toString())
                .build()
            val dateTap = Intent(Intent.ACTION_VIEW, calendarUri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            views.setOnClickPendingIntent(R.id.date_strip, activity(context, 2, dateTap))

            return views
        }

        private fun activity(context: Context, code: Int, intent: Intent): PendingIntent =
            PendingIntent.getActivity(
                context, code, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
    }
}
