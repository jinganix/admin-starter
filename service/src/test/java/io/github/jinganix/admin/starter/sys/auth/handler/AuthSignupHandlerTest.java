package io.github.jinganix.admin.starter.sys.auth.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("AuthSignupHandler")
class AuthSignupHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AuthSignupHandler authSignupHandler;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired AdminUserTokenRepository adminUserTokenRepository;

  @Autowired UserRoleRepository userRoleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException USERNAME_EXISTS when existing username")
  void shouldThrowApiExceptionUsernameExistsWhenExistingUsername() {
    // Given
    String username = "existing-user";
    testHelper.insertEntities(user(UID_1), userIdentity(UID_1).setUsername(username));

    // When / Then
    assertThatThrownBy(
            () -> authSignupHandler.handle(new AuthSignupRequest(username, "password123")))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USERNAME_EXISTS));
  }

  @Test
  @DisplayName("should throw runtime exception when uid generation failed")
  void shouldThrowRuntimeExceptionWhenUidGenerationFailed() {
    // Given
    String username = "new-user";
    String password = "password123";
    when(uidGenerator.nextUid()).thenThrow(new IllegalStateException("uid generation failed"));

    // When / Then
    assertThatThrownBy(() -> authSignupHandler.handle(new AuthSignupRequest(username, password)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("uid generation failed");
  }

  @Test
  @DisplayName("should create user and return token response when new username")
  void shouldCreateUserAndReturnTokenResponseWhenNewUsername() {
    // Given
    String username = "new-user";
    String password = "password123";
    String refreshToken = "refresh-token";
    String accessToken = "access-token";
    when(uidGenerator.nextUid()).thenReturn(UID_1, 20001L);
    when(utilsService.uuid(true)).thenReturn(refreshToken);
    when(tokenService.generate(anyLong())).thenReturn(accessToken);

    // When
    AuthTokenResponse response =
        authSignupHandler.handle(new AuthSignupRequest(username, password));

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new AuthTokenResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5)));
    assertThat(adminUserIdentityRepository.findByUsername(username)).isNotNull();
    assertThat(adminUserTokenRepository.findByRefreshToken(refreshToken)).isNotNull();
  }

  @Test
  @DisplayName("should signup user without admin role when register-as-admin disabled")
  void shouldSignupUserWithoutAdminRoleWhenRegisterAsAdminDisabled() {
    // Given
    String username = "plain-user";
    String password = "password123";
    testHelper.insertEntities(role(UID_1).setCode(RoleCode.ADMIN.name()));
    when(uidGenerator.nextUid()).thenReturn(UID_2, UID_3);
    when(utilsService.uuid(true)).thenReturn("refresh-token");
    when(tokenService.generate(anyLong())).thenReturn("access-token");
    ReflectionTestUtils.setField(authSignupHandler, "registerAsAdmin", false);

    // When
    try {
      authSignupHandler.handle(new AuthSignupRequest(username, password));
    } finally {
      ReflectionTestUtils.setField(authSignupHandler, "registerAsAdmin", true);
    }

    // Then
    assertThat(adminUserIdentityRepository.findByUsername(username)).isNotNull();
    assertThat(userRoleRepository.findAllByUserId(UID_2)).isEmpty();
  }
}
