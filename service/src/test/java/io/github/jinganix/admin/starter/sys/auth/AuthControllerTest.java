package io.github.jinganix.admin.starter.sys.auth;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.auth.AuthData.userToken;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuthController")
class AuthControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;
  @Autowired AdminUserTokenRepository adminUserTokenRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("login")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Login {

    private Stream<AuthLoginRequest> invalidRequests() {
      return Stream.of(
          new AuthLoginRequest(null, null),
          new AuthLoginRequest("ab", "123456"),
          new AuthLoginRequest("abcdefghijklmnopqrstu", "123456"),
          new AuthLoginRequest("aaaaaa", null),
          new AuthLoginRequest("aaaaaa", "12345"),
          new AuthLoginRequest("aaaaaa", "123456789012345678901"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @DisplayName("Given invalid request -> response BAD_REQUEST")
    void givenInvalidRequest(AuthLoginRequest request) throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(request), ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("Given no api authorities -> response token")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
      when(uidGenerator.nextUid()).thenReturn(UID_1);
      when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
      doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

      // When / Then
      testHelper
          .request(UID_1, new AuthLoginRequest("aaaaaa", "aaaaaa"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("signup")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Signup {

    private Stream<AuthSignupRequest> invalidRequests() {
      return Stream.of(
          new AuthSignupRequest(null, null),
          new AuthSignupRequest("ab", "123456"),
          new AuthSignupRequest("abcdefghijklmnopqrstu", "123456"),
          new AuthSignupRequest("aaaaaa", null),
          new AuthSignupRequest("aaaaaa", "12345"),
          new AuthSignupRequest("aaaaaa", "123456789012345678901"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @DisplayName("Given invalid request -> response BAD_REQUEST")
    void givenInvalidRequest(AuthSignupRequest request) throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(request), ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("Given no api authorities -> response token")
    void givenValidRequest() throws Exception {
      // Given
      when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
      when(uidGenerator.nextUid()).thenReturn(UID_1);
      when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
      doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

      // When / Then
      testHelper
          .request(UID_1, new AuthSignupRequest("aaaaaa", "aaaaaa"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("token")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Token {

    private Stream<AuthTokenRequest> invalidRequests() {
      return Stream.of(
          new AuthTokenRequest(),
          new AuthTokenRequest(""),
          new AuthTokenRequest("abcdefghijklmnopqrstuvwxyzabcdefghijklmno"));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @DisplayName("Given invalid request -> response BAD_REQUEST")
    void givenInvalidRequest(AuthTokenRequest request) throws Exception {
      // Given / When / Then
      testHelper.expectError(testHelper.request(UID_1, request), ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("Given no api authorities -> response token")
    void givenValidRequest() throws Exception {
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
}
