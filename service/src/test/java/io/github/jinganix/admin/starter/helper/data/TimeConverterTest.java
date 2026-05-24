package io.github.jinganix.admin.starter.helper.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TimeConverter")
class TimeConverterTest {

  private final TimeConverter converter = new TimeConverter();

  @Nested
  @DisplayName("when converting to database column")
  class WhenConvertingToDatabaseColumn {

    @Test
    @DisplayName("should return null when null date time")
    void shouldReturnNullWhenNullDateTime() {
      assertThat(converter.from(null)).isNull();
    }

    @Test
    @DisplayName("should return epoch millis when date time")
    void shouldReturnEpochMillisWhenDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

      assertThat(converter.from(dateTime)).isEqualTo(1704038400000L);
    }
  }

  @Nested
  @DisplayName("when converting to entity attribute")
  class WhenConvertingToEntityAttribute {

    @Test
    @DisplayName("should return null when null timestamp")
    void shouldReturnNullWhenNullTimestamp() {
      assertThat(converter.to(null)).isNull();
    }

    @Test
    @DisplayName("should return local date time when timestamp")
    void shouldReturnLocalDateTimeWhenTimestamp() {
      assertThat(converter.to(1704038400000L)).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
    }
  }

  @Test
  @DisplayName("should exposes supported types when converter")
  void shouldExposesSupportedTypesWhenConverter() {
    assertThat(converter.fromType()).isEqualTo(LocalDateTime.class);
    assertThat(converter.toType()).isEqualTo(Long.class);
  }
}
