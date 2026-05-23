package io.github.jinganix.admin.starter.sys.permission.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PermissionType.DbConverter")
class PermissionTypeDbConverterTest {

  private final PermissionType.DbConverter converter = new PermissionType.DbConverter();

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
      assertThat(converter.from((byte) 1)).isEqualTo(PermissionType.API);
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
      assertThat(converter.to(PermissionType.API)).isEqualTo((byte) 1);
    }
  }
}
