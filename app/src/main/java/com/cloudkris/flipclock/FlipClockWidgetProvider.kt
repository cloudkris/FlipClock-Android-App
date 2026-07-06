package com.cloudkris.flipclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Resizable home-screen widget. RemoteViews can't do the 3D flip
 * animation the in-app version has, so this shows a static flap-style
 * snapshot of the current time/date and refreshes on a timer.
 */
class FlipClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
        scheduleNextTick(context)
    }

    override fun onEnabled(context: Context) {
        scheduleNextTick(context)
    }

    override fun onDisabled(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(tickPendingIntent(context))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TICK) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, FlipClockWidgetProvider::class.java)
            )
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
            scheduleNextTick(context)
        }
    }

    private fun scheduleNextTick(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Fires roughly once a minute; exact timing isn't required for a clock widget.
        am.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 60_000,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15,
            tickPendingIntent(context)
        )
    }

    private fun tickPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, FlipClockWidgetProvider::class.java).apply {
            action = ACTION_TICK
        }
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val cal = Calendar.getInstance()
        val hourStr = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
        val minuteStr = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
        val dateStr = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
        val dayName = SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase()
        val monthName = SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase()

        val views = RemoteViews(context.packageName, R.layout.widget_flip_clock).apply {
            setTextViewText(R.id.widget_hour, hourStr)
            setTextViewText(R.id.widget_minute, minuteStr)
            setTextViewText(R.id.widget_dayname, dayName)
            setTextViewText(R.id.widget_date, dateStr)
            setTextViewText(R.id.widget_month, monthName)
        }
        manager.updateAppWidget(widgetId, views)
    }

    companion object {
        private const val ACTION_TICK = "com.cloudkris.flipclock.action.TICK"
    }
}
