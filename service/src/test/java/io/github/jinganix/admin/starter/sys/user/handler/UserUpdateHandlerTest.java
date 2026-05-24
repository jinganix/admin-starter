package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.user.UserData.userDetailsPb;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserUpdateHandler")
class UserUpdateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserUpdateHandler userUpdateHandler;

  @Autowired AdminService adminService;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired UserRepository userRepository;

  @Autowired UserRoleRepository userRoleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowApiExceptionWhenUserNotFound() {
    // Given
    UserUpdateRequest request =
        (UserUpdateRequest)
            new UserUpdateRequest()
                .setId(UID_1)
                .setNickname("updated")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(emptyList());

    // When / Then
    assertThatThrownBy(() -> userUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }

  @Test
  @DisplayName("should keep other user roles unchanged when target user not found")
  void shouldKeepOtherUserRolesUnchangedWhenTargetUserNotFound() {
    // Given
    testHelper.insertEntities(
        user(UID_2).setNickname("foo"),
        userIdentity(UID_2),
        role(UID_3),
        (UserRole)
            new UserRole()
                .setId(UID_4)
                .setUserId(UID_2)
                .setRoleId(UID_3)
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));
    UserUpdateRequest request =
        (UserUpdateRequest)
            new UserUpdateRequest()
                .setId(UID_1)
                .setNickname("updated")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(List.of(UID_3));

    // When / Then
    assertThatThrownBy(() -> userUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
    assertThat(userRoleRepository.findAllByUserId(UID_2))
        .extracting(UserRole::getRoleId)
        .containsExactly(UID_3);
  }

  @Test
  @DisplayName("should throw ApiException when admin user")
  void shouldThrowApiExceptionWhenAdminUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    Long adminUserId =
        adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME).getUserId();
    UserUpdateRequest request =
        (UserUpdateRequest)
            new UserUpdateRequest()
                .setId(adminUserId)
                .setNickname("updated")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(emptyList());

    // When / Then
    assertThatThrownBy(() -> userUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
    assertThat(userRepository.findByIdWithUsername(adminUserId).getUser().getNickname())
        .isEqualTo(AdminService.ADMIN_USERNAME);
    assertThat(userRoleRepository.findAllByUserId(adminUserId))
        .extracting(UserRole::getRoleId)
        .containsExactly(UID_1);
  }

  @Test
  @DisplayName("should return updated user when existing user")
  void shouldReturnUpdatedUserWhenExistingUser() {
    // Given
    testHelper.insertEntities(
        user(UID_1).setNickname("foo"),
        userIdentity(UID_1),
        role(UID_2),
        role(UID_3),
        (UserRole)
            new UserRole()
                .setId(UID_4)
                .setUserId(UID_1)
                .setRoleId(UID_2)
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));
    UserUpdateRequest request =
        (UserUpdateRequest)
            new UserUpdateRequest()
                .setId(UID_1)
                .setNickname("updated")
                .setStatus(UserStatus.ACTIVE)
                .setRoleIds(List.of(UID_3));

    // When
    UserUpdateResponse response = userUpdateHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            new UserUpdateResponse()
                .setUser(userDetailsPb(UID_1, "user-10001", "updated", List.of(UID_3))));
    assertThat(userRoleRepository.findAllByUserId(UID_1))
        .extracting(UserRole::getRoleId)
        .containsExactly(UID_3);
  }
}
