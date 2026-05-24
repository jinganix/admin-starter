package io.github.jinganix.admin.starter.setup.exception;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListRequest;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.sys.user.UserData;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;
  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return BAD_REQUEST when invalid request body")
  void shouldReturnBadRequestWhenInvalidRequestBody() throws Exception {
    // Given / When / Then
    testHelper
        .request(new AuthLoginRequest(null, null))
        .andExpect(status().isBadRequest())
        .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
  }

  @Test
  @DisplayName("should return BAD_REFRESH_TOKEN when invalid refresh token")
  void shouldReturnBadRefreshTokenWhenInvalidRefreshToken() throws Exception {
    // Given / When / Then
    testHelper
        .request(new AuthTokenRequest("missing-refresh-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_REFRESH_TOKEN));
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing permission")
  void shouldReturnAccessDeniedWhenMissingPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new OverviewListRequest())
        .andExpect(status().isForbidden())
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return BAD_CREDENTIAL when wrong password")
  void shouldReturnBadCredentialWhenWrongPassword() throws Exception {
    // Given
    testHelper.insertEntities(
        user(UID_1),
        UserData.userIdentity(UID_1)
            .setUsername("aaaaaa")
            .setPassword(passwordEncoder.encode("correct-password")));

    // When / Then
    testHelper
        .request(new AuthLoginRequest("aaaaaa", "wrong-password"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_CREDENTIAL));
  }

  @Test
  @DisplayName("should return USER_IS_INACTIVE when inactive user")
  void shouldReturnUserIsInactiveWhenInactiveUser() throws Exception {
    // Given
    testHelper.insertEntities(
        user(UID_1).setStatus(UserStatus.INACTIVE),
        UserData.userIdentity(UID_1)
            .setUsername("aaaaaa")
            .setPassword(passwordEncoder.encode("correct-password")));

    // When / Then
    testHelper
        .request(new AuthLoginRequest("aaaaaa", "correct-password"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.USER_IS_INACTIVE));
  }

  @Test
  @DisplayName("should return ERROR when unexpected runtime exception")
  void shouldReturnErrorWhenUnexpectedRuntimeException() throws Exception {
    // Given
    doThrow(new RuntimeException("boom")).when(credentialsAuthenticator).authenticate(any());

    // When / Then
    testHelper
        .request(new AuthLoginRequest("aaaaaa", "aaaaaa"))
        .andExpect(status().isInternalServerError())
        .andExpect(testHelper.isError(ErrorCode.ERROR));
  }
}
