package com.ikiugu.skytrackserver.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
  private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

  public static LocalDate instantToLocalDate(Instant instant, ZoneId zoneId) {
    return instant.atZone(zoneId).toLocalDate();
  }

  public static Instant localDateToInstant(LocalDate localDate, ZoneId zoneId) {
    return localDate.atStartOfDay(zoneId).toInstant();
  }

  public static String formatLocalDate(LocalDate date) {
    return date.format(ISO_DATE_FORMATTER);
  }

  public static LocalDate parseLocalDate(String dateString) {
    return LocalDate.parse(dateString, ISO_DATE_FORMATTER);
  }
}
