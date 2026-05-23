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
  @DisplayName("Given invalid request body -> response BAD_REQUEST")
  void givenValidationError() throws Exception {
    // Given / When / Then
    testHelper
        .request(new AuthLoginRequest(null, null))
        .andExpect(status().isBadRequest())
        .andExpect(testHelper.isError(ErrorCode.BAD_REQUEST));
  }

  @Test
  @DisplayName("Given invalid refresh token -> response BAD_REFRESH_TOKEN")
  void givenApiException() throws Exception {
    // Given / When / Then
    testHelper
        .request(new AuthTokenRequest("missing-refresh-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_REFRESH_TOKEN));
  }

  @Test
  @DisplayName("Given missing permission -> response ACCESS_DENIED")
  void givenAccessDeniedException() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new OverviewListRequest())
        .andExpect(status().isForbidden())
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("Given wrong password -> response BAD_CREDENTIAL")
  void givenAuthenticationException() throws Exception {
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
  @DisplayName("Given inactive user -> response USER_IS_INACTIVE")
  void givenDisabledException() throws Exception {
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
  @DisplayName("Given unexpected runtime exception -> response ERROR")
  void givenUnknownException() throws Exception {
    // Given
    doThrow(new RuntimeException("boom")).when(credentialsAuthenticator).authenticate(any());

    // When / Then
    testHelper
        .request(new AuthLoginRequest("aaaaaa", "aaaaaa"))
        .andExpect(status().isInternalServerError())
        .andExpect(testHelper.isError(ErrorCode.ERROR));
  }
}
