package io.github.jinganix.admin.starter.sys.user;

import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.sys.auth.model.AuthProvider;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("UserService")
class UserServiceTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserService userService;

  @Autowired RoleRepository roleRepository;

  @Autowired UserRoleRepository userRoleRepository;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given empty role codes when create user -> skip user role relations")
  void givenEmptyRoleCodesWhenCreateUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2);

    // When
    User user = userService.createUser("new-user", "password123", MILLIS);

    // Then
    assertThat(user.getId()).isEqualTo(UID_1);
    assertThat(userRoleRepository.findAllByUserId(UID_1)).isEmpty();
    assertThat(adminUserIdentityRepository.findByUserId(UID_1))
        .isNotNull()
        .satisfies(
            identity -> {
              assertThat(identity.getUsername()).isEqualTo("new-user");
              assertThat(identity.getProvider()).isEqualTo(AuthProvider.USERNAME);
              assertThat(passwordEncoder.matches("password123", identity.getPassword())).isTrue();
            });
  }

  @Test
  @DisplayName("Given role codes when create user -> create user role relations")
  void givenRoleCodesWhenCreateUser() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    roleRepository.insert(
        (Role)
            new Role()
                .setId(UID_3)
                .setCode(RoleCode.ADMIN.name())
                .setName("admin")
                .setDescription("admin role")
                .setStatus(RoleStatus.ACTIVE)
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));
    roleRepository.insert(
        (Role)
            new Role()
                .setId(UID_4)
                .setCode(RoleCode.AUTHED_USER.name())
                .setName("authed-user")
                .setDescription("authed role")
                .setStatus(RoleStatus.ACTIVE)
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));

    // When
    User user =
        userService.createUser(
            "new-user",
            "new-user",
            "password123",
            List.of(RoleCode.ADMIN, RoleCode.AUTHED_USER),
            MILLIS);

    // Then
    assertThat(user.getId()).isEqualTo(UID_1);
    assertThat(user.getNickname()).isEqualTo("new-user");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(adminUserIdentityRepository.findByUserId(UID_1))
        .isNotNull()
        .satisfies(
            identity -> {
              assertThat(identity.getUsername()).isEqualTo("new-user");
              assertThat(identity.getProvider()).isEqualTo(AuthProvider.USERNAME);
              assertThat(passwordEncoder.matches("password123", identity.getPassword())).isTrue();
            });
    assertThat(userRoleRepository.findAllByUserId(UID_1))
        .extracting(x -> x.getRoleId())
        .containsExactlyInAnyOrder(UID_3, UID_4);
  }

  @Test
  @DisplayName("Given existing identity when change password -> update encoded password")
  void givenExistingIdentityWhenChangePassword() {
    // Given
    String oldPassword = "old-password";
    String newPassword = "new-password";
    testHelper.insertEntities(userIdentity(UID_1).setPassword(passwordEncoder.encode(oldPassword)));

    // When
    userService.changePassword(UID_1, newPassword, MILLIS);

    // Then
    assertThat(
            passwordEncoder.matches(
                newPassword, adminUserIdentityRepository.findByUserId(UID_1).getPassword()))
        .isTrue();
  }

  @Test
  @DisplayName("Given user not found when change password -> throw ApiException")
  void givenUserNotFoundWhenChangePassword() {
    // Given
    long missingUserId = UID_1;

    // When / Then
    assertThatThrownBy(() -> userService.changePassword(missingUserId, "new-password", MILLIS))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }
}
