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
