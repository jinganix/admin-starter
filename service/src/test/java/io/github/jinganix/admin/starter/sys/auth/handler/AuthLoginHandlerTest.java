package io.github.jinganix.admin.starter.sys.auth.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("AuthLoginHandler")
class AuthLoginHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AuthLoginHandler authLoginHandler;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given username not found -> throw UsernameNotFoundException")
  void givenUsernameNotFound() {
    // Given
    AuthLoginRequest request = new AuthLoginRequest("nonexistent-user", "password123");

    // When / Then
    assertThatThrownBy(() -> authLoginHandler.handle(request))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("nonexistent-user");
  }

  @Test
  @DisplayName("Given valid credentials -> return token response")
  void givenValidCredentials() {
    // Given
    String username = "test-user";
    String password = "password123";
    String refreshToken = "refresh-token";
    String accessToken = "access-token";
    when(utilsService.uuid(true)).thenReturn(refreshToken);
    when(tokenService.generate(anyLong())).thenReturn(accessToken);
    testHelper.insertEntities(
        user(UID_1).setId(UID_1),
        userIdentity(UID_1).setUsername(username).setPassword(passwordEncoder.encode(password)));

    // When
    AuthTokenResponse response = authLoginHandler.handle(new AuthLoginRequest(username, password));

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new AuthTokenResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5)));
  }
}
