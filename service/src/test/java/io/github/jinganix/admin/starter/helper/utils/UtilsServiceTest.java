package io.github.jinganix.admin.starter.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UtilsService")
class UtilsServiceTest {

  private final UtilsService utilsService = new UtilsService();

  @Test
  @DisplayName("should return valid timestamp when current time")
  void shouldReturnValidTimestampWhenCurrentTime() {
    long millis = utilsService.currentTimeMillis();
    assertThat(millis).isGreaterThan(0);
  }

  @Test
  @DisplayName("should return true when current timestamp")
  void shouldReturnTrueWhenCurrentTimestamp() {
    long now = utilsService.currentTimeMillis();
    assertThat(utilsService.isToday(now)).isTrue();
  }

  @Test
  @DisplayName("should return false when yesterday timestamp")
  void shouldReturnFalseWhenYesterdayTimestamp() {
    long yesterday = utilsService.currentTimeMillis() - 86_400_000L;
    assertThat(utilsService.isToday(yesterday)).isFalse();
  }

  @Test
  @DisplayName("should return true when timestamps on same day")
  void shouldReturnTrueWhenTimestampsOnSameDay() {
    long time1 = 1704067200000L;
    long time2 = 1704110400000L;
    assertThat(utilsService.isSameDay(time1, time2)).isTrue();
  }

  @Test
  @DisplayName("should return false when timestamps on different days")
  void shouldReturnFalseWhenTimestampsOnDifferentDays() {
    long time1 = 1704067200000L;
    long time2 = 1704153600000L;
    assertThat(utilsService.isSameDay(time1, time2)).isFalse();
  }

  @Test
  @DisplayName("should return expected element when list and index")
  void shouldReturnExpectedElementWhenListAndIndex() {
    List<String> values = List.of("a", "b", "c");
    assertThat(utilsService.getOneOrLast(values, 0)).isEqualTo("a");
    assertThat(utilsService.getOneOrLast(values, 2)).isEqualTo("c");
    assertThat(utilsService.getOneOrLast(values, 10)).isEqualTo("c");
  }

  @Test
  @DisplayName("should return correct day difference when two timestamps")
  void shouldReturnCorrectDayDifferenceWhenTwoTimestamps() {
    long time1 = 1704067200000L;
    long time2 = 1704326400000L;
    assertThat(utilsService.getDiffDays(time1, time2)).isEqualTo(3);
  }

  @Test
  @DisplayName("should return 0 when same day timestamps")
  void shouldReturn0WhenSameDayTimestamps() {
    long time1 = 1704067200000L;
    long time2 = 1704110400000L;
    assertThat(utilsService.getDiffDays(time1, time2)).isEqualTo(0);
  }

  @Test
  @DisplayName("should return correct day difference when two dates")
  void shouldReturnCorrectDayDifferenceWhenTwoDates() {
    LocalDate date1 = LocalDate.of(2024, 1, 1);
    LocalDate date2 = LocalDate.of(2024, 1, 10);
    assertThat(utilsService.daysBetween(date1, date2)).isEqualTo(9);
  }

  @Test
  @DisplayName("should return 0 when same dates")
  void shouldReturn0WhenSameDates() {
    LocalDate date = LocalDate.of(2024, 1, 1);
    assertThat(utilsService.daysBetween(date, date)).isEqualTo(0);
  }

  @Test
  @DisplayName("should return correct timestamp when LocalDateTime")
  void shouldReturnCorrectTimestampWhenLocalDateTime() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    long millis = utilsService.toMillis(dateTime);
    assertThat(millis).isEqualTo(1704038400000L);
  }

  @Test
  @DisplayName("should return correct timestamp at start of day when LocalDate")
  void shouldReturnCorrectTimestampAtStartOfDayWhenLocalDate() {
    LocalDate date = LocalDate.of(2024, 1, 1);
    long millis = utilsService.toMillis(date);
    assertThat(millis).isEqualTo(1704038400000L);
  }

  @Test
  @DisplayName("should return current date when call")
  void shouldReturnCurrentDateWhenCall() {
    assertThat(utilsService.nowDate()).isNotNull();
  }

  @Test
  @DisplayName("should return current date time when call")
  void shouldReturnCurrentDateTimeWhenCall() {
    assertThat(utilsService.nowDatetime()).isNotNull();
  }

  @Test
  @DisplayName("should return UUID with dashes when with dash")
  void shouldReturnUuidWithDashesWhenWithDash() {
    String uuid = utilsService.uuid(true);
    assertThat(uuid).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
  }

  @Test
  @DisplayName("should return UUID without dashes when without dash")
  void shouldReturnUuidWithoutDashesWhenWithoutDash() {
    String uuid = utilsService.uuid(false);
    assertThat(uuid).matches("[0-9a-f]{32}");
    assertThat(uuid).doesNotContain("-");
  }

  @Test
  @DisplayName("should return unique UUIDs when multiple calls")
  void shouldReturnUniqueUUIDsWhenMultipleCalls() {
    String uuid1 = utilsService.uuid(false);
    String uuid2 = utilsService.uuid(false);
    assertThat(uuid1).isNotEqualTo(uuid2);
  }
}
