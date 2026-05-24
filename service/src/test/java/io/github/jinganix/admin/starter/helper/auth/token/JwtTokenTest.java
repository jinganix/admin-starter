package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtToken")
class JwtTokenTest {

  @Test
  @DisplayName("should return true when token with uuid")
  void shouldReturnTrueWhenTokenWithUuid() {
    JwtToken token = JwtToken.of(1L, "uuid", List.of("ROLE_USER"));

    assertThat(token.isValid()).isTrue();
  }

  @Test
  @DisplayName("should return false when invalid token")
  void shouldReturnFalseWhenInvalidToken() {
    assertThat(JwtToken.INVALID_TOKEN.isValid()).isFalse();
  }

  @Test
  @DisplayName("should exposes fields when values")
  void shouldExposesFieldsWhenValues() {
    JwtToken token = JwtToken.of(2L, "token-uuid", List.of("ROLE_ADMIN"));

    assertThat(token.getUserId()).isEqualTo(2L);
    assertThat(token.getUuid()).isEqualTo("token-uuid");
    assertThat(token.getAuthorities()).containsExactly("ROLE_ADMIN");
  }
}
