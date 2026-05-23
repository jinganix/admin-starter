package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("JwtToken")
class JwtTokenTest {

  @Nested
  @DisplayName("isValid")
  class IsValid {

    @Test
    @DisplayName("Given token with uuid -> returns true")
    void givenTokenWithUuid() {
      JwtToken token = JwtToken.of(1L, "uuid", List.of("ROLE_USER"));

      assertThat(token.isValid()).isTrue();
    }

    @Test
    @DisplayName("Given invalid token -> returns false")
    void givenInvalidToken() {
      assertThat(JwtToken.INVALID_TOKEN.isValid()).isFalse();
    }
  }

  @Nested
  @DisplayName("of")
  class Of {

    @Test
    @DisplayName("Given values -> exposes fields")
    void givenValues() {
      JwtToken token = JwtToken.of(2L, "token-uuid", List.of("ROLE_ADMIN"));

      assertThat(token.getUserId()).isEqualTo(2L);
      assertThat(token.getUuid()).isEqualTo("token-uuid");
      assertThat(token.getAuthorities()).containsExactly("ROLE_ADMIN");
    }
  }
}
