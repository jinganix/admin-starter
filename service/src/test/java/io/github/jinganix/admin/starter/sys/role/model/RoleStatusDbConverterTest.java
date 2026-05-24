package io.github.jinganix.admin.starter.sys.role.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoleStatus.DbConverter")
class RoleStatusDbConverterTest {

  private final RoleStatus.DbConverter converter = new RoleStatus.DbConverter();

  @Nested
  @DisplayName("from")
  class From {

    @Test
    @DisplayName("Given null -> returns null")
    void givenNull() {
      assertThat(converter.from(null)).isNull();
    }

    @Test
    @DisplayName("Given value -> returns enum")
    void givenValue() {
      assertThat(converter.from((byte) 1)).isEqualTo(RoleStatus.ACTIVE);
    }
  }

  @Nested
  @DisplayName("to")
  class To {

    @Test
    @DisplayName("Given null -> returns null")
    void givenNull() {
      assertThat(converter.to(null)).isNull();
    }

    @Test
    @DisplayName("Given enum -> returns byte")
    void givenEnum() {
      assertThat(converter.to(RoleStatus.ACTIVE)).isEqualTo((byte) 1);
    }
  }
}
