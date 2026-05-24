package io.github.jinganix.admin.starter.sys.auth;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.auth.AuthData.userToken;
import static io.github.jinganix.admin.starter.tests.InvalidRequestCase.badRequest;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import io.github.jinganix.admin.starter.sys.user.UserData;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.tests.InvalidRequestCase;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("AuthController")
class AuthControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;
  @Autowired AdminUserTokenRepository adminUserTokenRepository;
  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when login request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenLoginRequestIsInvalid {

    private Stream<InvalidRequestCase<AuthLoginRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              new AuthLoginRequest(null, null),
              "should return bad request when username and password are null"),
          badRequest(
              new AuthLoginRequest("ab", "123456"),
              "should return bad request when username below min length (3)"),
          badRequest(
              new AuthLoginRequest("abcdefghijklmnopqrstu", "123456"),
              "should return bad request when username above max length (20)"),
          badRequest(
              new AuthLoginRequest("aaaaaa", null),
              "should return bad request when password is null"),
          badRequest(
              new AuthLoginRequest("aaaaaa", "12345"),
              "should return bad request when password below min length (6)"),
          badRequest(
              new AuthLoginRequest("aaaaaa", "123456789012345678901"),
              "should return bad request when password above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<AuthLoginRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return BAD_TOKEN when malformed bearer token")
  void shouldReturnBadTokenWhenMalformedBearerToken() throws Exception {
    // Given
    String malformedToken = "malformed-token";

    // When / Then
    testHelper
        .request(malformedToken, new AuthLoginRequest("aaaaaa", "aaaaaa"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_TOKEN));
    verify(credentialsAuthenticator, never()).authenticate(any());
  }

  @Test
  @DisplayName("should return BAD_TOKEN when expired bearer token")
  void shouldReturnBadTokenWhenExpiredBearerToken() throws Exception {
    // Given
    String token = tokenService.generate(UID_1);
    when(utilsService.currentTimeMillis())
        .thenReturn(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8));

    // When / Then
    testHelper
        .request(token, new AuthLoginRequest("aaaaaa", "aaaaaa"))
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_TOKEN));
    verify(credentialsAuthenticator, never()).authenticate(any());
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
  @DisplayName("should return token when no api authorities")
  void shouldReturnTokenWhenNoApiAuthoritiesOnLogin() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
    doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

    // When / Then
    testHelper.request(UID_1, new AuthLoginRequest("aaaaaa", "aaaaaa")).andExpect(status().isOk());
    verify(tokenService, atLeastOnce()).decode(anyString());
    verify(credentialsAuthenticator, atLeastOnce()).authenticate(any());
  }

  @Nested
  @DisplayName("when signup request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenSignupRequestIsInvalid {

    private Stream<InvalidRequestCase<AuthSignupRequest>> invalidRequests() {
      return Stream.of(
          badRequest(
              new AuthSignupRequest(null, null),
              "should return bad request when username and password are null"),
          badRequest(
              new AuthSignupRequest("ab", "123456"),
              "should return bad request when username below min length (3)"),
          badRequest(
              new AuthSignupRequest("abcdefghijklmnopqrstu", "123456"),
              "should return bad request when username above max length (20)"),
          badRequest(
              new AuthSignupRequest("aaaaaa", null),
              "should return bad request when password is null"),
          badRequest(
              new AuthSignupRequest("aaaaaa", "12345"),
              "should return bad request when password below min length (6)"),
          badRequest(
              new AuthSignupRequest("aaaaaa", "123456789012345678901"),
              "should return bad request when password above max length (20)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<AuthSignupRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return token when no api authorities")
  void shouldReturnTokenWhenNoApiAuthoritiesOnSignup() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
    doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

    // When / Then
    testHelper.request(UID_1, new AuthSignupRequest("aaaaaa", "aaaaaa")).andExpect(status().isOk());
  }

  @Nested
  @DisplayName("when token request is invalid")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WhenTokenRequestIsInvalid {

    private Stream<InvalidRequestCase<AuthTokenRequest>> invalidRequests() {
      return Stream.of(
          badRequest(new AuthTokenRequest(), "should return bad request when refreshToken is null"),
          badRequest(
              new AuthTokenRequest(""), "should return bad request when refreshToken is blank"),
          badRequest(
              new AuthTokenRequest("abcdefghijklmnopqrstuvwxyzabcdefghijklmno"),
              "should return bad request when refreshToken above max length (40)"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestWhenRequestIsInvalid(InvalidRequestCase<AuthTokenRequest> testCase)
        throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, testCase.request()), testCase.errorCode());
    }
  }

  @Test
  @DisplayName("should return token when no api authorities")
  void shouldReturnTokenWhenNoApiAuthoritiesOnTokenRefresh() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
    testHelper.insertEntities(user(UID_1));
    adminUserTokenRepository.insert(userToken(UID_1).setRefreshToken("abc"));
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");

    // When / Then
    testHelper.request(UID_1, new AuthTokenRequest("abc")).andExpect(status().isOk());
  }
}
