package utils.date

import java.time.ZoneId

//TODO: tests, look into dates
object Conversions {
  def javaToJoda(time: java.time.LocalDateTime): org.joda.time.LocalDateTime = {
    val zonedDateTime = time.atZone(ZoneId.systemDefault())
    val instant = zonedDateTime.toInstant
    val millis = instant.toEpochMilli
    new org.joda.time.LocalDateTime(millis)
  }

  def jodaToJava(time: org.joda.time.DateTime): java.time.LocalDateTime =
    java.time.LocalDateTime.of(time.getYear, time.getMonthOfYear, time.getDayOfMonth,
      time.getHourOfDay, time.getMinuteOfHour, time.getSecondOfMinute,
      time.getMillisOfSecond*1000000) // takes nano
}
