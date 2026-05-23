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

  @Nested
  @DisplayName("support")
  class Support {

    @Test
    @DisplayName("Given username/password authentication token -> return true")
    void givenUsernamePasswordAuthenticationToken() {
      // Given
      Authentication authentication =
          new UsernamePasswordAuthenticationToken("username", "password");

      // When
      boolean result = credentialsAuthenticator.support(authentication);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given non-username/password authentication token -> return false")
    void givenNonUsernamePasswordAuthenticationToken() {
      // Given
      Authentication authentication = new TestingAuthenticationToken("username", "password");

      // When
      boolean result = credentialsAuthenticator.support(authentication);

      // Then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("authenticate")
  class Authenticate {

    @Test
    @DisplayName("Given missing identity -> throw UsernameNotFoundException")
    void givenMissingIdentity() {
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
    @DisplayName("Given invalid password -> throw BadCredentialsException")
    void givenInvalidPassword() {
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
    @DisplayName("Given missing user -> throw UsernameNotFoundException")
    void givenMissingUser() {
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
    @DisplayName("Given inactive user -> throw DisabledException")
    void givenInactiveUser() {
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
    @DisplayName("Given active user and valid credentials -> return auth user token")
    void givenActiveUserAndValidCredentials() {
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
