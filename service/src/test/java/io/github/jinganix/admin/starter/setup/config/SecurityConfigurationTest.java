package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@DisplayName("SecurityConfiguration")
@SuppressWarnings("unchecked")
class SecurityConfigurationTest {

  private final SecurityConfiguration configuration =
      new SecurityConfiguration(mock(AuthenticationEntryPoint.class), mock(Customizer.class));

  @Nested
  @DisplayName("passwordEncoder")
  class PasswordEncoderBean {

    @Test
    @DisplayName("Given raw password -> encode and match")
    void givenRawPassword() {
      // Given
      PasswordEncoder encoder = configuration.passwordEncoder();
      String raw = "admin-test-secret";

      // When
      String encoded = encoder.encode(raw);

      // Then
      assertThat(encoder.matches(raw, encoded)).isTrue();
      assertThat(encoder.matches("wrong", encoded)).isFalse();
    }
  }
}
