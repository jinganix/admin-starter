package io.github.jinganix.admin.starter.helper.auth.authenticator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationManagerImpl")
class AuthenticationManagerImplTest {

  private Authenticator firstAuthenticator;

  private Authenticator secondAuthenticator;

  private AuthenticationManagerImpl authenticationManager;

  @BeforeEach
  void setup() {
    firstAuthenticator = mock(Authenticator.class);
    secondAuthenticator = mock(Authenticator.class);
    authenticationManager =
        new AuthenticationManagerImpl(List.of(firstAuthenticator, secondAuthenticator));
  }

  @Nested
  @DisplayName("authenticate")
  class Authenticate {

    @Test
    @DisplayName("Given supported authenticator -> should delegate authentication")
    void givenSupportedAuthenticatorShouldDelegateAuthentication() {
      // Given
      Authentication token = mock(Authentication.class);
      Authentication authenticated = mock(Authentication.class);
      when(firstAuthenticator.support(token)).thenReturn(false);
      when(secondAuthenticator.support(token)).thenReturn(true);
      when(secondAuthenticator.authenticate(token)).thenReturn(authenticated);

      // When
      Authentication result = authenticationManager.authenticate(token);

      // Then
      assertThat(result).isSameAs(authenticated);
      verify(secondAuthenticator).authenticate(token);
    }

    @Test
    @DisplayName("Given no matching authenticator -> should throw")
    void givenNoMatchingAuthenticatorShouldThrow() {
      // Given
      Authentication token = mock(Authentication.class);
      when(firstAuthenticator.support(token)).thenReturn(false);
      when(secondAuthenticator.support(token)).thenReturn(false);

      // When / Then
      assertThatThrownBy(() -> authenticationManager.authenticate(token))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Unhandled authentication");
    }
  }
}
