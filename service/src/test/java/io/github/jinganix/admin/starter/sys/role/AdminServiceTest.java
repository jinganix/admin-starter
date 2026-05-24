package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
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
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.auth.model.AuthProvider;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("AdminService")
class AdminServiceTest extends SpringBootIntegrationTests {

  private static final long RESET_PWD_WHEN_UPDATED_AT = 1235L;

  @Autowired TestHelper testHelper;

  @Autowired AdminService adminService;

  @Autowired RoleRepository roleRepository;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired UserRoleRepository userRoleRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when initializing admin data")
  class WhenInitializingAdminData {

    @Test
    @DisplayName("should initialize admin role and user when missing admin data")
    void shouldInitializeAdminRoleAndUserWhenMissingAdminData() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);

      // When
      adminService.initAdminData(MILLIS);

      // Then
      Role adminRole = roleRepository.findByCode(RoleCode.ADMIN.name());
      AdminUserIdentity adminIdentity =
          adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME);
      assertThat(adminRole).isNotNull();
      assertThat(adminRole.getId()).isEqualTo(UID_1);
      assertThat(adminIdentity).isNotNull();
      assertThat(adminIdentity.getUserId()).isEqualTo(UID_2);
      assertThat(userRoleRepository.findAllByUserId(UID_2))
          .extracting(UserRole::getRoleId)
          .containsExactly(UID_1);
      assertThat(adminService.isAdminRole(UID_1)).isTrue();
      assertThat(adminService.isAdminUser(UID_2)).isTrue();
    }

    @Test
    @DisplayName("should reset password when admin identity at reset marker")
    void shouldResetPasswordWhenAdminIdentityAtResetMarker() {
      // Given
      String oldPassword = "old-password";
      testHelper.insertEntities(
          role(UID_1).setCode(RoleCode.ADMIN.name()).setName(AdminService.ADMIN_ROLE_CODE),
          user(UID_2).setNickname(AdminService.ADMIN_USERNAME),
          (AdminUserIdentity)
              new AdminUserIdentity()
                  .setId(UID_3)
                  .setUserId(UID_2)
                  .setProvider(AuthProvider.USERNAME)
                  .setUsername(AdminService.ADMIN_USERNAME)
                  .setPassword(passwordEncoder.encode(oldPassword))
                  .setVerified(true)
                  .setCreatedAt(MIN_TIMESTAMP)
                  .setUpdatedAt(RESET_PWD_WHEN_UPDATED_AT));

      // When
      adminService.initAdminData(MILLIS);

      // Then
      AdminUserIdentity updatedIdentity = adminUserIdentityRepository.findByUserId(UID_2);
      assertThat(passwordEncoder.matches("aaaaaa", updatedIdentity.getPassword())).isTrue();
      assertThat(updatedIdentity.getUpdatedAt()).isEqualTo(MILLIS);
    }

    @Test
    @DisplayName("should keep password when admin identity not at reset marker")
    void shouldKeepPasswordWhenAdminIdentityNotAtResetMarker() {
      // Given
      String oldPassword = "old-password";
      testHelper.insertEntities(
          role(UID_1).setCode(RoleCode.ADMIN.name()).setName(AdminService.ADMIN_ROLE_CODE),
          user(UID_2).setNickname(AdminService.ADMIN_USERNAME),
          (AdminUserIdentity)
              new AdminUserIdentity()
                  .setId(UID_3)
                  .setUserId(UID_2)
                  .setProvider(AuthProvider.USERNAME)
                  .setUsername(AdminService.ADMIN_USERNAME)
                  .setPassword(passwordEncoder.encode(oldPassword))
                  .setVerified(true)
                  .setCreatedAt(MIN_TIMESTAMP)
                  .setUpdatedAt(MIN_TIMESTAMP));

      // When
      adminService.initAdminData(MILLIS);

      // Then
      AdminUserIdentity updatedIdentity = adminUserIdentityRepository.findByUserId(UID_2);
      assertThat(passwordEncoder.matches(oldPassword, updatedIdentity.getPassword())).isTrue();
      assertThat(updatedIdentity.getUpdatedAt()).isEqualTo(MIN_TIMESTAMP);
    }

    @Test
    @DisplayName("should keep existing admin role relation when existing admin data")
    void shouldKeepExistingAdminRoleRelationWhenExistingAdminData() {
      // Given
      testHelper.insertEntities(
          role(UID_1).setCode(RoleCode.ADMIN.name()).setName(AdminService.ADMIN_ROLE_CODE),
          user(UID_2).setNickname(AdminService.ADMIN_USERNAME),
          (AdminUserIdentity)
              new AdminUserIdentity()
                  .setId(UID_3)
                  .setUserId(UID_2)
                  .setProvider(AuthProvider.USERNAME)
                  .setUsername(AdminService.ADMIN_USERNAME)
                  .setPassword(passwordEncoder.encode("old-password"))
                  .setVerified(true)
                  .setCreatedAt(MIN_TIMESTAMP)
                  .setUpdatedAt(MIN_TIMESTAMP),
          (UserRole)
              new UserRole()
                  .setId(UID_4)
                  .setUserId(UID_2)
                  .setRoleId(UID_1)
                  .setCreatedAt(MIN_TIMESTAMP)
                  .setUpdatedAt(MIN_TIMESTAMP));

      // When
      adminService.initAdminData(MILLIS);

      // Then
      assertThat(userRoleRepository.findAllByUserId(UID_2))
          .extracting(UserRole::getRoleId)
          .containsExactly(UID_1);
      assertThat(adminService.isAdminRole(UID_1)).isTrue();
      assertThat(adminService.isAdminUser(UID_2)).isTrue();
    }
  }

  @Nested
  @DisplayName("when checking admin role in role list")
  class WhenCheckingAdminRoleInRoleList {

    @Test
    @DisplayName("should return true when role list contains admin role")
    void shouldReturnTrueWhenRoleListContainsAdminRole() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);
      UserRole adminRole = new UserRole().setRoleId(UID_1);
      UserRole nonAdminRole = new UserRole().setRoleId(UID_4);

      // When
      boolean result = adminService.hasAdminRole(List.of(nonAdminRole, adminRole));

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when role list without admin role")
    void shouldReturnFalseWhenRoleListWithoutAdminRole() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);
      UserRole nonAdminRole = new UserRole().setRoleId(UID_4);

      // When
      boolean result = adminService.hasAdminRole(List.of(nonAdminRole));

      // Then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when role list with non-admin role id")
    void shouldReturnFalseWhenRoleListWithNonAdminRoleId() {
      // Given
      UserRole role = new UserRole().setRoleId(Long.MAX_VALUE);

      // When
      boolean result = adminService.hasAdminRole(List.of(role));

      // Then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("when checking if role is admin")
  class WhenCheckingIfRoleIsAdmin {

    @Test
    @DisplayName("should return true when initialized admin role id")
    void shouldReturnTrueWhenInitializedAdminRoleId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminRole(UID_1);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when non-admin role id")
    void shouldReturnFalseWhenNonAdminRoleId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminRole(UID_4);

      // Then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("when checking if user is admin")
  class WhenCheckingIfUserIsAdmin {

    @Test
    @DisplayName("should return true when initialized admin user id")
    void shouldReturnTrueWhenInitializedAdminUserId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminUser(UID_2);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when non-admin user id")
    void shouldReturnFalseWhenNonAdminUserId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminUser(UID_4);

      // Then
      assertThat(result).isFalse();
    }
  }

  @Test
  @DisplayName("should throw ApiException USER_NOT_FOUND when missing admin identity")
  void shouldThrowApiExceptionUserNotFoundWhenMissingAdminIdentity() {
    // Given
    ReflectionTestUtils.setField(adminService, "adminUserId", UID_1);

    // When / Then
    assertThatThrownBy(
            () -> ReflectionTestUtils.invokeMethod(adminService, "resetAdminPassword", MILLIS))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
  }
}
