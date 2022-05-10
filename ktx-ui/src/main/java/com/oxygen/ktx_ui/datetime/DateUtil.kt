package com.oxygen.ktx_ui.datetime

import com.oxygen.ktx_ui.datetime.TimeUtil.DAY_S
import com.oxygen.ktx_ui.datetime.TimeUtil.HOUR_S
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToLong

object DateUtil {

    const val SDF_TYPE_1 = "dd MMM, HH:mm"
    const val SDF_TYPE_2 = "EEE dd.MM"
    const val SDF_TYPE_3 = "MMMM dd, yyyy"
    const val SDF_TYPE_4 = "dd MMM"
    const val SDF_TYPE_5 = "MMMM yyyy"
    const val SDF_TYPE_6 = "dd MMMM, yyyy"
    const val SDF_TYPE_7 = "yyyy-MM-dd'T'HH:mm:ss Z"
    const val SDF_TYPE_8 = "EEE dd MMMM"
    const val SDF_TYPE_9 = "HH:mm" // 24 hour format
    const val SDF_TYPE_10 = "dd MMMM yyyy, HH:mm"
    const val SDF_TYPE_11 = "dd MMM, HH:mm"
    const val SDF_TYPE_12 = "dd/MM/yyyy"
    const val SDF_TYPE_13 = "dd.MM.yyyy"
    const val SDF_TYPE_14 = "h:mm a"
    const val SDF_TYPE_15 = "EEE, h:mm a"
    const val SDF_TYPE_16 = "EEE, MMM d, h:mm a"
    const val SDF_TYPE_17 = "M/d/yy"
    const val SDF_TYPE_18 = "d MMM, HH:mm"
    const val SDF_TYPE_19 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val SDF_TYPE_20 = "E, dd MMM yyyy, HH:mm"
    const val SDF_TYPE_21 = "dd MMM"
    const val SDF_TYPE_22 = "dd/MM"
    const val SDF_TYPE_23 = "dd MMM, yyyy"
    const val SDF_TYPE_24 = "dd_MM_yyyy_HH_mm"
    const val SDF_TYPE_25 = "MMM yyyy"
    const val SDF_TYPE_26 = "EE d. MMM"
    const val SDF_TYPE_27 = "EE d. MMM yyyy"
    const val SDF_TYPE_28 = "yyyy-MM-dd"
    const val SDF_TYPE_29 = "HH:mm:ss"
    const val SDF_TYPE_30 = "EEE d MMM hh:mm a"
    const val SDF_TYPE_31 = "MM.yyyy"
    const val SDF_TYPE_32 = "EEE d MMM yyyy hh:mm a"
    const val SDF_TYPE_33 = "hh:mm" // 12 hour format
    const val SDF_TYPE_34 = "EEEE, MMM dd, yyyy"

    fun calculateDays(startTimeMills: Long, endTimeMills: Long, roundUp: Boolean): Long {
        val days = (endTimeMills.toDouble() - startTimeMills) / TimeUtil.DAY_MS
        return if (roundUp) ceil(days).toLong() else days.roundToLong()
    }

    /**
     * fromDay, toDay in EEEE format e.g. "Monday"
     */
    fun isTodayInBounds(fromDay: String, toDay: String): Boolean {
        val todayIndex = Calendar.getInstance(Locale.ENGLISH)[Calendar.DAY_OF_WEEK]

        val fromIndex = DateFormatSymbols.getInstance(Locale.getDefault())
            .weekdays.indexOfFirst { it.equals(fromDay, ignoreCase = true) }
        val toIndex = DateFormatSymbols.getInstance(Locale.getDefault())
            .weekdays.indexOfFirst { it.equals(toDay, ignoreCase = true) }

        return todayIndex in fromIndex..toIndex
    }

    fun changeFormat(time: String, oldFormat: String, newFormat: String): String {
        val sdfOld = SimpleDateFormat(oldFormat, Locale.US)
        val sdfNew = SimpleDateFormat(newFormat, Locale.US)

        val date = sdfOld.parse(time) ?: return ""
        return sdfNew.format(date)
    }

    fun isSameDay(firstTimestamp: Long, secondTimestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val startDate = calendar.copyWithTime(firstTimestamp)
        val endDate = calendar.copyWithTime(secondTimestamp)

        return isDateEqual(startDate, endDate)
    }

    fun isToday(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
        return isSameDay(timestamp, now)
    }

    fun isTomorrow(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance()
        val nextDay = calendar.copyWithTime(now).addDays(1)

        return isSameDay(timestamp, nextDay.timeInMillis)
    }

    fun isSameYear(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance()
        val nowDate = calendar.copyWithTime(now)
        val givenDate = calendar.copyWithTime(timestamp)

        return nowDate.hasEqualYear(givenDate)
    }

    fun isCurrentMonth(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance()
        val nowDate = calendar.copyWithTime(now)
        val givenDate = calendar.copyWithTime(timestamp)

        return nowDate.hasEqualYear(givenDate)
                && nowDate.hasEqualMonth(givenDate)
    }

    fun isBeforeNow(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
        return now - timestamp > 0
    }

    fun getNextDayOfWeek(now: Calendar, targetDayOfWeek: Int): Calendar {
        var diff = targetDayOfWeek - now[Calendar.DAY_OF_WEEK]
        // if day is same as today, search for same day of next week
        if (diff <= 0) {
            diff += 7
        }
        now.add(Calendar.DAY_OF_MONTH, diff)
        return now
    }

    /**
     * Checks if [firstDate] Calendar and [secondDate] Calendar are equal by date. Time doesn't matter.
     * For example 10.10.2020 10:10 and 10.10.2020 14:00 are equal by date.
     */
    private fun isDateEqual(
        firstDate: Calendar,
        secondDate: Calendar
    ): Boolean {
        return (firstDate.hasEqualYear(secondDate)
                && firstDate.hasEqualMonth(secondDate)
                && firstDate.hasEqualDayOfMonth(secondDate))
    }

    private fun Calendar.copyWithTime(timestamp: Long) = copy().apply { timeInMillis = timestamp }

    private fun Calendar.hasEqualYear(calendar: Calendar) = equalByField(calendar, Calendar.YEAR)

    private fun Calendar.hasEqualMonth(calendar: Calendar) = equalByField(calendar, Calendar.MONTH)

    private fun Calendar.hasEqualDayOfMonth(calendar: Calendar) =
        equalByField(calendar, Calendar.DAY_OF_MONTH)

    private fun Calendar.equalByField(calendar: Calendar, field: Int): Boolean =
        this[field] == calendar[field]
}

fun Calendar.copy(): Calendar = clone() as Calendar

fun Calendar.isTimeWithinWeekendBounds(): Boolean {
    val dayOfWeek = this[Calendar.DAY_OF_WEEK]
    val hourOfDay = this[Calendar.HOUR_OF_DAY]
    val minutes = this[Calendar.MINUTE]
    return dayOfWeek == Calendar.FRIDAY && hourOfDay >= 15
            || dayOfWeek == Calendar.SATURDAY
            || dayOfWeek == Calendar.SUNDAY
            || dayOfWeek == Calendar.MONDAY && (hourOfDay <= 13 || hourOfDay == 14 && minutes == 0)
}

fun Calendar.addMinutes(minutes: Int): Calendar = this.apply { add(Calendar.MINUTE, minutes) }

fun Calendar.addMinutes(minutes: Long): Calendar =
    this.apply { add(Calendar.MINUTE, minutes.toInt()) }

fun Calendar.addDays(days: Int): Calendar = copy().apply {
    add(Calendar.DAY_OF_MONTH, days)
}

fun Calendar.clearSeconds(): Calendar = this.apply {
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

/**
 * Rounds to closest next minutes interval.
 *
 * For example:
 * - 10:04 with interval 10min will round to 10:10.
 * - 10:10 with interval 10min will give us 10:10.
 * - 10:11 with interval 10min will give us 10:20.
 *
 * @return returns new Calendar object so it won't change existing object.
 */
fun Calendar.roundToClosestNextMinuteInterval(intervalInMinutes: Int): Calendar {
    return roundToClosestNextInterval(Calendar.MINUTE, intervalInMinutes)
}

/**
 * Rounds to closest next [interval] of selected [field].
 * Interval has to be larger than 0.
 *
 * @param field Calendar field for example [Calendar.MONTH]
 * @return returns new Calendar object so it won't change existing object.
 */
fun Calendar.roundToClosestNextInterval(
    field: Int,
    interval: Int
): Calendar {
    if (interval <= 0) throw IllegalArgumentException("Interval cannot be lower or equal than 0.")

    // copy calendar to not change original one
    val newCalendar = copy()

    val fieldValue = newCalendar.get(field)
    val roundToInterval = fieldValue % interval
    if (roundToInterval == 0) return newCalendar

    newCalendar.add(field, interval - roundToInterval)

    return newCalendar
}

data class TimeContainer(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)

/**
 * Returns days, hours, minutes, seconds from milliseconds
 */
fun Long.getDaysHoursMinutesSeconds(): TimeContainer {
    var totalSecs = this / 1000
    val days = totalSecs / DAY_S

    totalSecs %= 24 * 3600
    val hours = totalSecs / HOUR_S

    totalSecs %= 3600
    val minutes = totalSecs / 60

    val seconds = totalSecs % 60

    return TimeContainer(
        days,
        hours,
        minutes,
        seconds
    )
}

fun SimpleDateFormat.parseOrNull(source: String?): Date? = try {
    this.isLenient = false
    source?.let { parse(it) }
} catch (e: ParseException) {
    null
} catch (e: NullPointerException) {
    null
}

fun Date.isLeapYear() = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
