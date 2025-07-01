package com.app.base.mvvm.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object DateUtils {

  /**
   * Current date
   */
  private val today: Date
    get() = Date()

  const val SHORT_TIME_FORMAT = "HH:mm:ss"

  private val LOG_TAG = DateUtils::class.java.toString()

  fun convertStringToStringDate(fullTime: String): String {
    val cal = Calendar.getInstance(Locale.getDefault())
    var dateInstance: SimpleDateFormat? = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
      cal.time = dateInstance!!.parse(fullTime)
    } catch (e: ParseException) {
      e.printStackTrace()
      dateInstance = null
    }

    if (dateInstance == null) {
      dateInstance = SimpleDateFormat("yyyy:MM:dd", Locale.getDefault())
      try {
        cal.time = dateInstance.parse(fullTime)
      } catch (e: ParseException) {
        e.printStackTrace()
        return ""
      }
    }
    return toStringAs(cal.time, "yyyy:MM:dd")
  }

  fun convertStringToCalendar(date: String): Calendar? {
    val cal = Calendar.getInstance(Locale.getDefault())
    val formatDate = SimpleDateFormat("yy:MM:dd", Locale.getDefault())

    try {
      cal.time = formatDate.parse(date)
    } catch (e: ParseException) {
      return null
    }

    return cal
  }

  fun getDayOfWeek(stringDate: String): String? {
    val formatter = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
    try {
      val date = formatter.parse(stringDate)
      println(formatter.format(date!!))

      val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
      val dayOfWeek = simpleDateFormat.format(date)
      println(dayOfWeek)
      return dayOfWeek
    } catch (e: ParseException) {
      e.printStackTrace()
    }

    return null
  }

  fun lastDayOfYear(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DATE, 31)
    calendar.set(Calendar.MONTH, 11)
    return calendar.time
  }

  fun firstDayOfYear(): Date {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.set(Calendar.DATE, 1)
    calendar.set(Calendar.MONTH, 0)
    return calendar.time
  }

  /**
   * Convert String to date with format
   *
   * @param strDate String date
   * @param format  format
   * @return Date as format. if error will return now
   */
  fun toDate(strDate: String, format: String): Date {
    if (TextUtils.isEmpty(strDate)) {
      return Date()
    }
    val sf = SimpleDateFormat(format, Locale.getDefault())
    try {
      return sf.parse(strDate)
    } catch (e: ParseException) {
      e.printStackTrace()
    }

    return Date()
  }

  /**
   * Convert date time to string as format
   *
   * @param date   time
   * @param format format
   * @return Date time as string
   */
  fun toStringAs(date: Date, format: String): String {
    val sf = SimpleDateFormat(format, Locale.getDefault())
    return sf.format(date)
  }

  fun toStringAsUTC(date: Date, format: String): String {
    val sf = SimpleDateFormat(format, Locale.getDefault())
    sf.timeZone = TimeZone.getTimeZone("UTC")
    return sf.format(date)
  }

  fun toStringAs(time: Long, format: String): String {
    val sf = SimpleDateFormat(format, Locale.getDefault())
    return sf.format(time)
  }

  /**
   * Current time with format
   *
   * @param format format
   * @return string of time with format
   */
  fun nowAsFormat(format: String): String {
    return toStringAs(Date(), format)
  }

  /**
   * Current time with format
   *
   * @param format format
   * @return string of time with format
   */
  fun nowUTCAsFormat(format: String): String {
    return toStringAsUTC(Date(), format)
  }

  fun parseTime(year: Int, month: Int, day: Int): String {
    return String.format("%d/%02d/%02d", year, month, day)
  }

  @SuppressLint("SimpleDateFormat")
  fun convertLongToString(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy:MM:dd")
    return format.format(date)
  }

  @SuppressLint("SimpleDateFormat")
  fun convertLongToString(time: Long, format: String): String {
    val date = Date(time)
    val formatDate = SimpleDateFormat(format)
    formatDate.timeZone = TimeZone.getTimeZone("UTC")
    return formatDate.format(date)
  }

  @SuppressLint("SimpleDateFormat")
  fun convertLongToTimeString(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy:MM:dd HH:mm")
    return format.format(date)
  }

  @SuppressLint("SimpleDateFormat")
  fun convertStringToLong(time: String): Long {
    val date: Date? = try {
      val sdf = SimpleDateFormat(" HH:mm:ss")
      sdf.parse(time)
    } catch (e: ParseException) {
      e.printStackTrace()
      today
    }
    date?.let {
      return it.time
    }
    return today.time
  }

  fun getDateFromFullTime(time: String?): String {
    if (time.isNullOrBlank()) {
      return ""
    }
    val splits = time.split(" ")
    if (splits.size > 1) {
      return splits[0]
    }
    return time
  }

  fun getHourFromFullTime(time: String?): String {
    if (time.isNullOrBlank()) {
      return ""
    }
    val splits = time.split(" ")
    if (splits.size > 1) {
      return splits[1]
    }
    return time
  }

  fun convertStringToLocalTime(time: String): String {
//    val df = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH)
    val df = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(time)
    df.timeZone = TimeZone.getDefault()
    val formattedDate = date?.let { df.format(it) }
    Log.d(LOG_TAG, "TimeUTC= $time -> time Locale=$formattedDate")
    return formattedDate ?: time
  }

  fun convertStringToShortTimeLocalNoNeedDay(time: String): String {
    try {
//      val df = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH)
      val df = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
      df.timeZone = TimeZone.getTimeZone("UTC")
      val date = df.parse(time)
      df.timeZone = TimeZone.getDefault()
      val formattedDate = date?.let { df.format(it) }
      Log.d(LOG_TAG, "TimeUTC= $time -> time Locale=$formattedDate")

      date?.let {
        val hour = DateFormat.format("HH", date) as String // HH-> hh
        val minute = DateFormat.format("mm", date) as String
        return "$hour:$minute"
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return ""
  }

  private fun convertToHourMinute(time: String): Time? {
    try {
      val times = time.split(":")
      if (times.size != 2) return null
      return Time(hour = times[0].toInt(), minute = times[1].toInt())
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  data class Time constructor(var hour: Int, var minute: Int)

  fun dateAddDate(inputDate: String, addDate: Int): String {
    val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    println("Current Date $inputDate")

    val date = dateFormat.parse(inputDate)
    val c = Calendar.getInstance()
    date?.let {
      c.time = date
      c.add(Calendar.DATE, addDate)
    }

    val newDate = c.time

    val outPutDate = dateFormat.format(newDate)
    println("New Date $outPutDate")

    return outPutDate
  }
}
