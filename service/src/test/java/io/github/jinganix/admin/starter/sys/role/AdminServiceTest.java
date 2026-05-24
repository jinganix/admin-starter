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
  @DisplayName("initAdminData")
  class InitAdminData {

    @Test
    @DisplayName("Given missing admin data -> initialize admin role and user")
    void givenMissingAdminData() {
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
    @DisplayName("Given admin identity at reset marker -> reset password")
    void givenAdminIdentityAtResetMarker() {
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
    @DisplayName("Given admin identity not at reset marker -> keep password")
    void givenAdminIdentityNotAtResetMarker() {
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
    @DisplayName("Given existing admin data -> keep existing admin role relation")
    void givenExistingAdminData() {
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
  @DisplayName("hasAdminRole")
  class HasAdminRole {

    @Test
    @DisplayName("Given role list contains admin role -> return true")
    void givenRoleListContainsAdminRole() {
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
    @DisplayName("Given role list without admin role -> return false")
    void givenRoleListWithoutAdminRole() {
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
    @DisplayName("Given role list with non-admin role id -> return false")
    void givenRoleListWithNonAdminRoleId() {
      // Given
      UserRole role = new UserRole().setRoleId(Long.MAX_VALUE);

      // When
      boolean result = adminService.hasAdminRole(List.of(role));

      // Then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("isAdminRole")
  class IsAdminRole {

    @Test
    @DisplayName("Given initialized admin role id -> return true")
    void givenInitializedAdminRoleId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminRole(UID_1);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given non-admin role id -> return false")
    void givenNonAdminRoleId() {
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
  @DisplayName("isAdminUser")
  class IsAdminUser {

    @Test
    @DisplayName("Given initialized admin user id -> return true")
    void givenInitializedAdminUserId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminUser(UID_2);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given non-admin user id -> return false")
    void givenNonAdminUserId() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      boolean result = adminService.isAdminUser(UID_4);

      // Then
      assertThat(result).isFalse();
    }
  }

  @Nested
  @DisplayName("resetAdminPassword")
  class ResetAdminPassword {

    @Test
    @DisplayName("Given missing admin identity -> throw ApiException USER_NOT_FOUND")
    void givenMissingAdminIdentity() {
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
}
