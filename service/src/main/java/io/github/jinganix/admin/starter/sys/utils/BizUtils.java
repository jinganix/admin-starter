package io.github.jinganix.admin.starter.sys.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;

public class BizUtils {

  private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final DateTimeFormatter noFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static String format(LocalDateTime dateTime) {
    return dateTime == null ? null : formatter.format(dateTime);
  }

  public static String format(Long millis) {
    return format(toLocaDateTime(millis));
  }

  public static long toMillis(LocalDateTime dateTime) {
    return dateTime.atZone(SHANGHAI).toInstant().toEpochMilli();
  }

  public static LocalDateTime toLocaDateTime(Long millis) {
    return millis == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), SHANGHAI);
  }

  public static <T> boolean notEquals(Collection<T> a, Collection<T> b) {
    return !new HashSet<>(a).equals(new HashSet<>(b));
  }
}
