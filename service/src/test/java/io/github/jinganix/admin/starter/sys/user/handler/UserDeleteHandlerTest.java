package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserDeleteHandler")
class UserDeleteHandlerTest extends SpringBootIntegrationTests {

  private static final long NORMAL_USER_ID = 1101L;
  private static final long MISSING_USER_ID = 1102L;

  @Autowired TestHelper testHelper;

  @Autowired UserDeleteHandler userDeleteHandler;

  @Autowired AdminService adminService;

  @Autowired UserRepository userRepository;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    UserDeleteRequest request = new UserDeleteRequest().setIds(List.of(MISSING_USER_ID));

    // When / Then
    assertThatThrownBy(() -> userDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should throw ApiException when partially missing users")
  void shouldThrowApiExceptionWhenPartiallyMissingUsers() {
    // Given
    testHelper.insertEntities(
        user(NORMAL_USER_ID).setNickname("foo"), userIdentity(NORMAL_USER_ID));
    UserDeleteRequest request =
        new UserDeleteRequest().setIds(List.of(NORMAL_USER_ID, MISSING_USER_ID));

    // When / Then
    assertThatThrownBy(() -> userDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
    assertThat(userRepository.findByIdWithUsername(NORMAL_USER_ID)).isNotNull();
  }

  @Test
  @DisplayName("should throw ApiException when admin user")
  void shouldThrowApiExceptionWhenAdminUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    Long adminUserId =
        adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME).getUserId();
    UserDeleteRequest request = new UserDeleteRequest().setIds(List.of(adminUserId));

    // When / Then
    assertThatThrownBy(() -> userDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
  }

  @Test
  @DisplayName("should keep all users unchanged when request contains admin user")
  void shouldKeepAllUsersUnchangedWhenRequestContainsAdminUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    Long adminUserId =
        adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME).getUserId();
    testHelper.insertEntities(user(UID_5).setNickname("foo"), userIdentity(UID_5));
    UserDeleteRequest request = new UserDeleteRequest().setIds(List.of(adminUserId, UID_5));

    // When / Then
    assertThatThrownBy(() -> userDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
    assertThat(userRepository.findByIdWithUsername(UID_5)).isNotNull();
    assertThat(adminUserIdentityRepository.findByUserId(UID_5)).isNotNull();
  }

  @Test
  @DisplayName("should delete user when existing user")
  void shouldDeleteUserWhenExistingUser() {
    // Given
    testHelper.insertEntities(
        user(NORMAL_USER_ID).setNickname("foo"), userIdentity(NORMAL_USER_ID));
    UserDeleteRequest request = new UserDeleteRequest().setIds(List.of(NORMAL_USER_ID));

    // When
    UserDeleteResponse response = userDeleteHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new UserDeleteResponse());
    assertThat(userRepository.findByIdWithUsername(NORMAL_USER_ID)).isNull();
    assertThat(adminUserIdentityRepository.findByUserId(NORMAL_USER_ID)).isNull();
  }
}
