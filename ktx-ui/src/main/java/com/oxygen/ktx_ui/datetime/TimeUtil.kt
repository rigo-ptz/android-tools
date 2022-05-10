package com.oxygen.ktx_ui.datetime

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtil {

    const val SECOND_MS = 1000
    const val MINUTE_MS = 60 * SECOND_MS
    const val HOUR_MS = 60 * MINUTE_MS
    const val HOUR_S = HOUR_MS / SECOND_MS
    const val DAY_MS = 24 * HOUR_MS
    const val DAY_S = DAY_MS / SECOND_MS

    /**
     * @param startTimeHhMmSs string in [DateUtil.SDF_TYPE_29] format
     * @param endTimeHhMmSs string in [DateUtil.SDF_TYPE_29] format
     */
    fun isTimeInBounds(now: Calendar, startTimeHhMmSs: String, endTimeHhMmSs: String): Boolean {
        val format = SimpleDateFormat(DateUtil.SDF_TYPE_29, Locale.US)

        val fromCalendar = (format.parse(startTimeHhMmSs) ?: return false).let {
            Calendar.getInstance().apply {
                time = it
            }
        }
        val toCalendar = (format.parse(endTimeHhMmSs) ?: return false).let {
            Calendar.getInstance().apply {
                time = it
            }
        }

        fromCalendar.apply {
            set(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DATE])
        }

        toCalendar.apply {
            set(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DATE])
        }

        return now.after(fromCalendar) && now.before(toCalendar)
    }

    fun getDurationInMin(diffInMillis: Long): String {
        val diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val minutes = diffInMin.toInt()
        return minutes.toString()
    }

}
