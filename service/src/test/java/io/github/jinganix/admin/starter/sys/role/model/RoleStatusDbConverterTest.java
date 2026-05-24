package io.github.jinganix.admin.starter.sys.role.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoleStatus.DbConverter")
class RoleStatusDbConverterTest {

  private final RoleStatus.DbConverter converter = new RoleStatus.DbConverter();

  @Nested
  @DisplayName("when converting to entity attribute")
  class WhenConvertingToEntityAttribute {

    @Test
    @DisplayName("should return null when null")
    void shouldReturnNullWhenNull() {
      assertThat(converter.from(null)).isNull();
    }

    @Test
    @DisplayName("should return enum when value")
    void shouldReturnEnumWhenValue() {
      assertThat(converter.from((byte) 1)).isEqualTo(RoleStatus.ACTIVE);
    }
  }

  @Nested
  @DisplayName("when converting to database column")
  class WhenConvertingToDatabaseColumn {

    @Test
    @DisplayName("should return null when null")
    void shouldReturnNullWhenNull() {
      assertThat(converter.to(null)).isNull();
    }

    @Test
    @DisplayName("should return byte when enum")
    void shouldReturnByteWhenEnum() {
      assertThat(converter.to(RoleStatus.ACTIVE)).isEqualTo((byte) 1);
    }
  }
}
