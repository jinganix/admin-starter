package io.github.jinganix.admin.starter.sys.auth.authenticator;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("CredentialsAuthenticator")
class CredentialsAuthenticatorTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired CredentialsAuthenticator credentialsAuthenticator;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return true when username/password authentication token")
  void shouldReturnTrueWhenUsernamepasswordAuthenticationToken() {
    // Given
    Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password");

    // When
    boolean result = credentialsAuthenticator.support(authentication);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("should return false when non-username/password authentication token")
  void shouldReturnFalseWhenNonUsernamepasswordAuthenticationToken() {
    // Given
    Authentication authentication = new TestingAuthenticationToken("username", "password");

    // When
    boolean result = credentialsAuthenticator.support(authentication);

    // Then
    assertThat(result).isFalse();
  }

  @Nested
  @DisplayName("when authenticating credentials")
  class WhenAuthenticatingCredentials {

    @Test
    @DisplayName("should throw UsernameNotFoundException when missing identity")
    void shouldThrowUsernameNotFoundExceptionWhenMissingIdentity() {
      // Given
      String username = "missing-user";
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(username, "password123");

      // When / Then
      assertThatThrownBy(() -> credentialsAuthenticator.authenticate(authentication))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessage(username);
    }

    @Test
    @DisplayName("should throw BadCredentialsException when invalid password")
    void shouldThrowBadCredentialsExceptionWhenInvalidPassword() {
      // Given
      String username = "user-10001";
      testHelper.insertEntities(
          user(UID_1),
          userIdentity(UID_1)
              .setUsername(username)
              .setPassword(passwordEncoder.encode("password123")));
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(username, "wrong-password");

      // When / Then
      assertThatThrownBy(() -> credentialsAuthenticator.authenticate(authentication))
          .isInstanceOf(BadCredentialsException.class)
          .hasMessage("Invalid password");
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException when missing user")
    void shouldThrowUsernameNotFoundExceptionWhenMissingUser() {
      // Given
      String username = "identity-only-user";
      testHelper.insertEntities(
          userIdentity(UID_1)
              .setUsername(username)
              .setPassword(passwordEncoder.encode("password123")));
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(username, "password123");

      // When / Then
      assertThatThrownBy(() -> credentialsAuthenticator.authenticate(authentication))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessage(username);
    }

    @Test
    @DisplayName("should throw DisabledException when inactive user")
    void shouldThrowDisabledExceptionWhenInactiveUser() {
      // Given
      String username = "inactive-user";
      testHelper.insertEntities(
          user(UID_1).setStatus(UserStatus.INACTIVE),
          userIdentity(UID_1)
              .setUsername(username)
              .setPassword(passwordEncoder.encode("password123")));
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(username, "password123");

      // When / Then
      assertThatThrownBy(() -> credentialsAuthenticator.authenticate(authentication))
          .isInstanceOf(DisabledException.class)
          .hasMessage("User is inactive");
    }

    @Test
    @DisplayName("should return auth user token when active user and valid credentials")
    void shouldReturnAuthUserTokenWhenActiveUserAndValidCredentials() {
      // Given
      String username = "active-user";
      testHelper.insertEntities(
          user(UID_1),
          userIdentity(UID_1)
              .setUsername(username)
              .setPassword(passwordEncoder.encode("password123")));
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(username, "password123");

      // When
      AuthUserToken token = credentialsAuthenticator.authenticate(authentication);

      // Then
      assertThat(token.getUserId()).isEqualTo(UID_1);
    }
  }
}
