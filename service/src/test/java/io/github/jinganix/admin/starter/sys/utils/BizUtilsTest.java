package io.github.jinganix.admin.starter.sys.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BizUtils")
class BizUtilsTest {

  private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

  @Nested
  @DisplayName("when formatting dates")
  class WhenFormattingDates {

    @Test
    @DisplayName("should return null when null date time")
    void shouldReturnNullWhenNullDateTime() {
      assertThat(BizUtils.format((LocalDateTime) null)).isNull();
    }

    @Test
    @DisplayName("should return formatted string when date time")
    void shouldReturnFormattedStringWhenDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

      assertThat(BizUtils.format(dateTime)).isEqualTo("2024-01-15 10:30:00");
    }

    @Test
    @DisplayName("should return null when null millis")
    void shouldReturnNullWhenNullMillis() {
      assertThat(BizUtils.format((Long) null)).isNull();
    }

    @Test
    @DisplayName("should return formatted string when millis")
    void shouldReturnFormattedStringWhenMillis() {
      long millis =
          LocalDateTime.of(2024, 1, 15, 10, 30, 0).atZone(SHANGHAI).toInstant().toEpochMilli();

      assertThat(BizUtils.format(millis)).isEqualTo("2024-01-15 10:30:00");
    }
  }

  @Test
  @DisplayName("should return epoch millis when date time")
  void shouldReturnEpochMillisWhenDateTime() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

    assertThat(BizUtils.toMillis(dateTime))
        .isEqualTo(dateTime.atZone(SHANGHAI).toInstant().toEpochMilli());
  }

  @Test
  @DisplayName("should return null when null millis")
  void shouldReturnNullWhenNullMillisForToLocalDateTime() {
    assertThat(BizUtils.toLocaDateTime(null)).isNull();
  }

  @Test
  @DisplayName("should return local date time when millis")
  void shouldReturnLocalDateTimeWhenMillis() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    long millis = dateTime.atZone(SHANGHAI).toInstant().toEpochMilli();

    assertThat(BizUtils.toLocaDateTime(millis)).isEqualTo(dateTime);
  }

  @Test
  @DisplayName("should return true when different collections")
  void shouldReturnTrueWhenDifferentCollections() {
    assertThat(BizUtils.notEquals(List.of("a"), List.of("b"))).isTrue();
  }

  @Test
  @DisplayName("should return false when same collections")
  void shouldReturnFalseWhenSameCollections() {
    assertThat(BizUtils.notEquals(List.of("a", "b"), List.of("b", "a"))).isFalse();
  }
}
