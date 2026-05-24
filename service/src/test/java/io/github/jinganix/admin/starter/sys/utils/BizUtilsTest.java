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
  @DisplayName("format")
  class Format {

    @Test
    @DisplayName("Given null date time -> returns null")
    void givenNullDateTime() {
      assertThat(BizUtils.format((LocalDateTime) null)).isNull();
    }

    @Test
    @DisplayName("Given date time -> returns formatted string")
    void givenDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

      assertThat(BizUtils.format(dateTime)).isEqualTo("2024-01-15 10:30:00");
    }

    @Test
    @DisplayName("Given null millis -> returns null")
    void givenNullMillis() {
      assertThat(BizUtils.format((Long) null)).isNull();
    }

    @Test
    @DisplayName("Given millis -> returns formatted string")
    void givenMillis() {
      long millis =
          LocalDateTime.of(2024, 1, 15, 10, 30, 0).atZone(SHANGHAI).toInstant().toEpochMilli();

      assertThat(BizUtils.format(millis)).isEqualTo("2024-01-15 10:30:00");
    }
  }

  @Nested
  @DisplayName("toMillis")
  class ToMillis {

    @Test
    @DisplayName("Given date time -> returns epoch millis")
    void givenDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

      assertThat(BizUtils.toMillis(dateTime))
          .isEqualTo(dateTime.atZone(SHANGHAI).toInstant().toEpochMilli());
    }
  }

  @Nested
  @DisplayName("toLocaDateTime")
  class ToLocaDateTime {

    @Test
    @DisplayName("Given null millis -> returns null")
    void givenNullMillis() {
      assertThat(BizUtils.toLocaDateTime(null)).isNull();
    }

    @Test
    @DisplayName("Given millis -> returns local date time")
    void givenMillis() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
      long millis = dateTime.atZone(SHANGHAI).toInstant().toEpochMilli();

      assertThat(BizUtils.toLocaDateTime(millis)).isEqualTo(dateTime);
    }
  }

  @Nested
  @DisplayName("notEquals")
  class NotEquals {

    @Test
    @DisplayName("Given different collections -> returns true")
    void givenDifferentCollections() {
      assertThat(BizUtils.notEquals(List.of("a"), List.of("b"))).isTrue();
    }

    @Test
    @DisplayName("Given same collections -> returns false")
    void givenSameCollections() {
      assertThat(BizUtils.notEquals(List.of("a", "b"), List.of("b", "a"))).isFalse();
    }
  }
}
