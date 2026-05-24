package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePermission;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("RoleAuthorityService")
class RoleAuthorityServiceTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleAuthorityService roleAuthorityService;

  @Autowired AdminService adminService;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when resolving api authorities")
  class WhenResolvingApiAuthorities {

    @Test
    @DisplayName("should return built-in admin api authorities when admin user")
    void shouldReturnBuiltInAdminApiAuthoritiesWhenAdminUser() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);

      // When
      Set<GrantedAuthority> authorities = roleAuthorityService.getApiAuthorities(UID_2);

      // Then
      assertThat(authorities)
          .contains(RoleCode.ADMIN.authority(), RoleCode.AUTHED_USER.authority());
      assertThat(authorities).contains(new SimpleGrantedAuthority(Authority.SYS_ROLE_LIST.name()));
    }

    @Test
    @DisplayName(
        "should return authed user and api authority when active regular role with api permission")
    void shouldReturnAuthedUserAndApiAuthorityWhenActiveRegularRoleWithApiPermission() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          role(UID_2).setCode("OPS"),
          (UserRole)
              new UserRole()
                  .setId(UID_3)
                  .setUserId(UID_1)
                  .setRoleId(UID_2)
                  .setCreatedAt(MILLIS)
                  .setUpdatedAt(MILLIS),
          permission(UID_4)
              .setCode(Authority.SYS_ROLE_LIST.getValue())
              .setType(PermissionType.API)
              .setStatus(PermissionStatus.ACTIVE),
          rolePermission(UID_5, UID_2, UID_4));

      // When
      Set<GrantedAuthority> authorities = roleAuthorityService.getApiAuthorities(UID_1);

      // Then
      assertThat(authorities)
          .contains(
              RoleCode.AUTHED_USER.authority(),
              new SimpleGrantedAuthority(Authority.SYS_ROLE_LIST.name()))
          .doesNotContain(RoleCode.ADMIN.authority());
    }

    @Test
    @DisplayName("should return authed user authority only when no roles")
    void shouldReturnAuthedUserAuthorityOnlyWhenNoRoles() {
      // Given
      testHelper.insertEntities(user(UID_1));

      // When
      Set<GrantedAuthority> authorities = roleAuthorityService.getApiAuthorities(UID_1);

      // Then
      assertThat(authorities).containsExactly(RoleCode.AUTHED_USER.authority());
    }

    @Test
    @DisplayName(
        "should ignore role and permission authorities when inactive role with api permission")
    void shouldIgnoreRoleAndPermissionAuthoritiesWhenInactiveRoleWithApiPermission() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          role(UID_2).setCode("OPS").setStatus(RoleStatus.INACTIVE),
          (UserRole)
              new UserRole()
                  .setId(UID_3)
                  .setUserId(UID_1)
                  .setRoleId(UID_2)
                  .setCreatedAt(MILLIS)
                  .setUpdatedAt(MILLIS),
          permission(UID_4)
              .setCode(Authority.SYS_ROLE_LIST.getValue())
              .setType(PermissionType.API)
              .setStatus(PermissionStatus.ACTIVE),
          rolePermission(UID_5, UID_2, UID_4));

      // When
      Set<GrantedAuthority> authorities = roleAuthorityService.getApiAuthorities(UID_1);

      // Then
      assertThat(authorities).containsExactly(RoleCode.AUTHED_USER.authority());
    }
  }

  @Nested
  @DisplayName("when resolving ui authorities")
  class WhenResolvingUiAuthorities {

    @Test
    @DisplayName("should return admin role and all ui permissions when admin user")
    void shouldReturnAdminRoleAndAllUiPermissionsWhenAdminUser() {
      // Given
      when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
      adminService.initAdminData(MILLIS);
      testHelper.insertEntities(
          permission(UID_3)
              .setCode("ui:dashboard")
              .setType(PermissionType.UI)
              .setStatus(PermissionStatus.ACTIVE),
          permission(UID_4)
              .setCode("ui:hidden")
              .setType(PermissionType.UI)
              .setStatus(PermissionStatus.INACTIVE),
          permission(UID_5)
              .setCode(Authority.SYS_ROLE_LIST.getValue())
              .setType(PermissionType.API)
              .setStatus(PermissionStatus.ACTIVE));

      // When
      Set<String> authorities = roleAuthorityService.getUiAuthorities(UID_2);

      // Then
      assertThat(authorities)
          .contains(
              RoleCode.AUTHED_USER.getCode(), RoleCode.ADMIN.getCode(), "ui:dashboard", "ui:hidden")
          .doesNotContain(Authority.SYS_ROLE_LIST.getValue());
    }

    @Test
    @DisplayName(
        "should return role authority and active ui permissions when active regular role with ui permissions")
    void shouldReturnRoleAuthorityAndActiveUiPermissionsWhenActiveRegularRoleWithUiPermissions() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          role(UID_2).setCode("OPS"),
          (UserRole)
              new UserRole()
                  .setId(UID_3)
                  .setUserId(UID_1)
                  .setRoleId(UID_2)
                  .setCreatedAt(MILLIS)
                  .setUpdatedAt(MILLIS),
          permission(UID_4)
              .setCode("ui:ops")
              .setType(PermissionType.UI)
              .setStatus(PermissionStatus.ACTIVE),
          permission(UID_5)
              .setCode("ui:disabled")
              .setType(PermissionType.UI)
              .setStatus(PermissionStatus.INACTIVE),
          rolePermission(UID_1, UID_2, UID_4),
          rolePermission(UID_2, UID_2, UID_5));

      // When
      Set<String> authorities = roleAuthorityService.getUiAuthorities(UID_1);

      // Then
      assertThat(authorities)
          .contains(RoleCode.AUTHED_USER.getCode(), "ROLE_OPS", "ui:ops")
          .doesNotContain("ui:disabled", RoleCode.ADMIN.getCode());
    }

    @Test
    @DisplayName("should return authed user authority only when no roles")
    void shouldReturnAuthedUserAuthorityOnlyWhenNoRoles() {
      // Given
      testHelper.insertEntities(user(UID_1));

      // When
      Set<String> authorities = roleAuthorityService.getUiAuthorities(UID_1);

      // Then
      assertThat(authorities).containsExactly(RoleCode.AUTHED_USER.getCode());
    }

    @Test
    @DisplayName("should ignore role and ui permissions when inactive role with ui permission")
    void shouldIgnoreRoleAndUiPermissionsWhenInactiveRoleWithUiPermission() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          role(UID_2).setCode("OPS").setStatus(RoleStatus.INACTIVE),
          (UserRole)
              new UserRole()
                  .setId(UID_3)
                  .setUserId(UID_1)
                  .setRoleId(UID_2)
                  .setCreatedAt(MILLIS)
                  .setUpdatedAt(MILLIS),
          permission(UID_4)
              .setCode("ui:ops")
              .setType(PermissionType.UI)
              .setStatus(PermissionStatus.ACTIVE),
          rolePermission(UID_5, UID_2, UID_4));

      // When
      Set<String> authorities = roleAuthorityService.getUiAuthorities(UID_1);

      // Then
      assertThat(authorities).containsExactly(RoleCode.AUTHED_USER.getCode());
    }
  }
}
