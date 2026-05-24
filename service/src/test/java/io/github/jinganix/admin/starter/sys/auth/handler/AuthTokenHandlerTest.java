package io.github.jinganix.admin.starter.sys.auth.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.auth.AuthData.userToken;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuthTokenHandler")
class AuthTokenHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AuthTokenHandler authTokenHandler;

  @Autowired AdminUserTokenRepository adminUserTokenRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return empty response when empty refresh token")
  void shouldReturnEmptyResponseWhenEmptyRefreshToken() {
    // Given
    String refreshToken = "existing-refresh-token";
    adminUserTokenRepository.insert(userToken(UID_1).setRefreshToken(refreshToken));

    // When
    AuthTokenResponse response = authTokenHandler.handle(new AuthTokenRequest(""));

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new AuthTokenResponse());
    assertThat(adminUserTokenRepository.findByRefreshToken(refreshToken)).isNotNull();
  }

  @Test
  @DisplayName("should throw ApiException BAD_REFRESH_TOKEN when unknown refresh token")
  void shouldThrowApiExceptionBadRefreshTokenWhenUnknownRefreshToken() {
    // Given
    AuthTokenRequest request = new AuthTokenRequest("unknown-refresh-token");

    // When / Then
    assertThatThrownBy(() -> authTokenHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.BAD_REFRESH_TOKEN));
  }

  @Test
  @DisplayName("should throw ApiException USER_NOT_FOUND when token for deleted user")
  void shouldThrowApiExceptionUserNotFoundWhenTokenForDeletedUser() {
    // Given
    String refreshToken = "refresh-token-for-deleted-user";
    adminUserTokenRepository.insert(userToken(UID_1).setRefreshToken(refreshToken));

    // When / Then
    assertThatThrownBy(() -> authTokenHandler.handle(new AuthTokenRequest(refreshToken)))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should return new token response when valid refresh token")
  void shouldReturnNewTokenResponseWhenValidRefreshToken() {
    // Given
    String oldRefreshToken = "old-refresh-token";
    String newRefreshToken = "new-refresh-token";
    String accessToken = "access-token";
    when(utilsService.uuid(true)).thenReturn(newRefreshToken);
    when(tokenService.generate(anyLong())).thenReturn(accessToken);
    testHelper.insertEntities(user(UID_1));
    adminUserTokenRepository.insert(userToken(UID_1).setRefreshToken(oldRefreshToken));

    // When
    AuthTokenResponse response = authTokenHandler.handle(new AuthTokenRequest(oldRefreshToken));

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new AuthTokenResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(newRefreshToken)
                .setExpiresIn(MILLIS + TimeUnit.MINUTES.toMillis(5)));
    assertThat(adminUserTokenRepository.findByRefreshToken(oldRefreshToken)).isNull();
    assertThat(adminUserTokenRepository.findByRefreshToken(newRefreshToken)).isNotNull();
  }
}
