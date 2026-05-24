package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserUpdateStatusHandler")
class UserUpdateStatusHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserUpdateStatusHandler userUpdateStatusHandler;

  @Autowired AdminService adminService;

  @Autowired UserRepository userRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    UserUpdateStatusRequest request = new UserUpdateStatusRequest(UID_1, UserStatus.INACTIVE);

    // When / Then
    assertThatThrownBy(() -> userUpdateStatusHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should throw ApiException when admin user")
  void shouldThrowApiExceptionWhenAdminUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    UserUpdateStatusRequest request = new UserUpdateStatusRequest(UID_2, UserStatus.INACTIVE);

    // When / Then
    assertThatThrownBy(() -> userUpdateStatusHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
  }

  @Test
  @DisplayName("should return updated user response when existing user")
  void shouldReturnUpdatedUserResponseWhenExistingUser() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));
    UserUpdateStatusRequest request = new UserUpdateStatusRequest(UID_1, UserStatus.INACTIVE);

    // When
    UserUpdateStatusResponse response = userUpdateStatusHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new UserUpdateStatusResponse(
                new UserPb()
                    .setId(UID_1)
                    .setUsername("user-10001")
                    .setNickname("foo")
                    .setStatus(UserStatus.INACTIVE)
                    .setCreatedAt(MIN_TIMESTAMP)));
  }

  @Test
  @DisplayName("should fail and keep original status when null status")
  void shouldFailAndKeepOriginalStatusWhenNullStatus() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("foo"), userIdentity(UID_1));
    UserUpdateStatusRequest request = new UserUpdateStatusRequest(UID_1, null);

    // When / Then
    assertThatThrownBy(() -> userUpdateStatusHandler.handle(request))
        .isInstanceOf(RuntimeException.class);
    assertThat(userRepository.findById(UID_1).getStatus())
        .isEqualTo(io.github.jinganix.admin.starter.sys.user.model.UserStatus.ACTIVE);
  }
}
