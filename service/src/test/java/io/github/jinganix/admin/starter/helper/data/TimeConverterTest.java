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
  @DisplayName("from")
  class From {

    @Test
    @DisplayName("Given null date time -> returns null")
    void givenNullDateTime() {
      assertThat(converter.from(null)).isNull();
    }

    @Test
    @DisplayName("Given date time -> returns epoch millis")
    void givenDateTime() {
      LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

      assertThat(converter.from(dateTime)).isEqualTo(1704038400000L);
    }
  }

  @Nested
  @DisplayName("to")
  class To {

    @Test
    @DisplayName("Given null timestamp -> returns null")
    void givenNullTimestamp() {
      assertThat(converter.to(null)).isNull();
    }

    @Test
    @DisplayName("Given timestamp -> returns local date time")
    void givenTimestamp() {
      assertThat(converter.to(1704038400000L)).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
    }
  }

  @Nested
  @DisplayName("types")
  class Types {

    @Test
    @DisplayName("Given converter -> exposes supported types")
    void givenConverter() {
      assertThat(converter.fromType()).isEqualTo(LocalDateTime.class);
      assertThat(converter.toType()).isEqualTo(Long.class);
    }
  }
}
