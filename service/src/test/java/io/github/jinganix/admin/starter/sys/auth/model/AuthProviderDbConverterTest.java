package io.github.jinganix.admin.starter.sys.auth.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthProvider.DbConverter")
class AuthProviderDbConverterTest {

  private final AuthProvider.DbConverter converter = new AuthProvider.DbConverter();

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
      assertThat(converter.from((byte) 0)).isEqualTo(AuthProvider.USERNAME);
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
      assertThat(converter.to(AuthProvider.USERNAME)).isEqualTo((byte) 0);
    }
  }
}
