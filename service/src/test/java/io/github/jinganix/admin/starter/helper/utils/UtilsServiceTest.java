package io.github.jinganix.admin.starter.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UtilsService")
class UtilsServiceTest {

  private final UtilsService utilsService = new UtilsService();

  @Nested
  @DisplayName("currentTimeMillis")
  class CurrentTimeMillis {

    @Test
    @DisplayName("Given current time -> returns valid timestamp")
    void givenCurrentTime() {
      long millis = utilsService.currentTimeMillis();
      assertThat(millis).isGreaterThan(0);
    }
  }

  @Nested
  @DisplayName("isToday")
  class IsToday {

    @Test
    @DisplayName("Given current timestamp -> returns true")
    void givenCurrentTimestamp() {
      long now = utilsService.currentTimeMillis();
      assertThat(utilsService.isToday(now)).isTrue();
    }

    @Test
    @DisplayName("Given yesterday timestamp -> returns false")
    void givenYesterdayTimestamp() {
      long yesterday = utilsService.currentTimeMillis() - 86_400_000L;
      assertThat(utilsService.isToday(yesterday)).isFalse();
    }
  }

  @Nested
  @DisplayName("isSameDay")
  class IsSameDay {

    @Test
    @DisplayName("Given timestamps on same day -> returns true")
    void givenTimestampsOnSameDay() {
      long time1 = 1704067200000L;
      long time2 = 1704110400000L;
      assertThat(utilsService.isSameDay(time1, time2)).isTrue();
    }

    @Test
    @DisplayName("Given timestamps on different days -> returns false")
    void givenTimestampsOnDifferentDays() {
      long time1 = 1704067200000L;
      long time2 = 1704153600000L;
      assertThat(utilsService.isSameDay(time1, time2)).isFalse();
    }
  }

  @Nested
  @DisplayName("getOneOrLast")
  class GetOneOrLast {

    @Test
    @DisplayName("Given list and index -> returns expected element")
    void givenListAndIndex() {
      List<String> values = List.of("a", "b", "c");
      assertThat(utilsService.getOneOrLast(values, 0)).isEqualTo("a");
      assertThat(utilsService.getOneOrLast(values, 2)).isEqualTo("c");
      assertThat(utilsService.getOneOrLast(values, 10)).isEqualTo("c");
    }
  }

  @Nested
  @DisplayName("getDiffDays")
  class GetDiffDays {

    @Test
    @DisplayName("Given two timestamps -> returns correct day difference")
    void givenTwoTimestamps() {
      long time1 = 1704067200000L;
      long time2 = 1704326400000L;
      assertThat(utilsService.getDiffDays(time1, time2)).isEqualTo(3);
    }

    @Test
    @DisplayName("Given same day timestamps -> returns 0")
    void givenSameDay() {
      long time1 = 1704067200000L;
      long time2 = 1704110400000L;
      assertThat(utilsService.getDiffDays(time1, time2)).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("daysBetween")
  class DaysBetween {

    @Test
    @DisplayName("Given two dates -> returns correct day difference")
    void givenTwoDates() {
      LocalDate date1 = LocalDate.of(2024, 1, 1);
      LocalDate date2 = LocalDate.of(2024, 1, 10);
      assertThat(utilsService.daysBetween(date1, date2)).isEqualTo(9);
    }

    @Test
    @DisplayName("Given same dates -> returns 0")
    void givenSameDates() {
      LocalDate date = LocalDate.of(2024, 1, 1);
      assertThat(utilsService.daysBetween(date, date)).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("toMillis")
  class ToMillis {

    @Test
    @DisplayName("Given LocalDateTime -> returns correct timestamp")
    void givenLocalDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
      long millis = utilsService.toMillis(dateTime);
      assertThat(millis).isEqualTo(1704038400000L);
    }

    @Test
    @DisplayName("Given LocalDate -> returns correct timestamp at start of day")
    void givenLocalDate() {
      LocalDate date = LocalDate.of(2024, 1, 1);
      long millis = utilsService.toMillis(date);
      assertThat(millis).isEqualTo(1704038400000L);
    }
  }

  @Nested
  @DisplayName("nowDate")
  class NowDate {

    @Test
    @DisplayName("Given call -> returns current date")
    void givenCall() {
      assertThat(utilsService.nowDate()).isNotNull();
    }
  }

  @Nested
  @DisplayName("nowDatetime")
  class NowDatetime {

    @Test
    @DisplayName("Given call -> returns current date time")
    void givenCall() {
      assertThat(utilsService.nowDatetime()).isNotNull();
    }
  }

  @Nested
  @DisplayName("uuid")
  class Uuid {

    @Test
    @DisplayName("Given with dash -> returns UUID with dashes")
    void givenWithDash() {
      String uuid = utilsService.uuid(true);
      assertThat(uuid).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("Given without dash -> returns UUID without dashes")
    void givenWithoutDash() {
      String uuid = utilsService.uuid(false);
      assertThat(uuid).matches("[0-9a-f]{32}");
      assertThat(uuid).doesNotContain("-");
    }

    @Test
    @DisplayName("Given multiple calls -> returns unique UUIDs")
    void givenMultipleCalls() {
      String uuid1 = utilsService.uuid(false);
      String uuid2 = utilsService.uuid(false);
      assertThat(uuid1).isNotEqualTo(uuid2);
    }
  }
}
