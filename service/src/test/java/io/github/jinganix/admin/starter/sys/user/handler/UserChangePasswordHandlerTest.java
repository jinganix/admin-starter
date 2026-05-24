package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("UserChangePasswordHandler")
class UserChangePasswordHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserChangePasswordHandler userChangePasswordHandler;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    UserChangePasswordRequest request =
        new UserChangePasswordRequest().setCurrent("password123").setPassword("newpassword1");

    // When / Then
    assertThatThrownBy(() -> userChangePasswordHandler.handle(UID_1, request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should throw ApiException when current password mismatch")
  void shouldThrowApiExceptionWhenCurrentPasswordMismatch() {
    // Given
    String currentPassword = "password123";
    testHelper.insertEntities(
        user(UID_1), userIdentity(UID_1).setPassword(passwordEncoder.encode(currentPassword)));
    UserChangePasswordRequest request =
        new UserChangePasswordRequest().setCurrent("wrong-password").setPassword("newpassword1");

    // When / Then
    assertThatThrownBy(() -> userChangePasswordHandler.handle(UID_1, request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PASSWORD_NOT_MATCH));
    assertThat(
            passwordEncoder.matches(
                currentPassword, adminUserIdentityRepository.findByUserId(UID_1).getPassword()))
        .isTrue();
  }

  @Test
  @DisplayName("should change password when valid current password")
  void shouldChangePasswordWhenValidCurrentPassword() {
    // Given
    String currentPassword = "password123";
    String newPassword = "newpassword1";
    testHelper.insertEntities(
        user(UID_1), userIdentity(UID_1).setPassword(passwordEncoder.encode(currentPassword)));
    UserChangePasswordRequest request =
        new UserChangePasswordRequest().setCurrent(currentPassword).setPassword(newPassword);

    // When
    UserChangePasswordResponse response = userChangePasswordHandler.handle(UID_1, request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new UserChangePasswordResponse());
    assertThat(
            passwordEncoder.matches(
                newPassword, adminUserIdentityRepository.findByUserId(UID_1).getPassword()))
        .isTrue();
  }
}
