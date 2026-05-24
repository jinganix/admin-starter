package io.github.jinganix.admin.starter.sys.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.permission.PermissionService;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("DataInitializer")
class DataInitializerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AdminService adminService;

  @Autowired OverviewRepository overviewRepository;

  @Autowired PermissionRepository permissionRepository;

  @Autowired PermissionService permissionService;

  @Autowired RoleRepository roleRepository;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @Autowired UserRoleRepository userRoleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should initialize creates baseline data when empty database")
  void shouldInitializeCreatesBaselineDataWhenEmptyDatabase() {
    // Given
    DataInitializer dataInitializer =
        new DataInitializer(
            adminService, overviewRepository, permissionService, uidGenerator, utilsService);

    // When
    dataInitializer.initialize();

    // Then
    LocalDate upperBoundMonth = LocalDate.now().withDayOfMonth(1).plusMonths(12);
    assertThat(overviewRepository.findAllByMonthBefore(upperBoundMonth)).hasSize(18);
    assertThat(permissionRepository.findAll()).hasSize(Authority.values().length);
    assertThat(roleRepository.findByCode(AdminService.ADMIN_ROLE_CODE)).isNotNull();
    AdminUserIdentity identity =
        adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME);
    assertThat(identity).isNotNull();
    assertThat(userRoleRepository.findAllByUserId(identity.getUserId())).hasSize(1);
  }

  @Test
  @DisplayName("should initialize keeps data idempotent when initialized database")
  void shouldInitializeKeepsDataIdempotentWhenInitializedDatabase() {
    // Given
    DataInitializer dataInitializer =
        new DataInitializer(
            adminService, overviewRepository, permissionService, uidGenerator, utilsService);
    dataInitializer.initialize();

    // When
    dataInitializer.initialize();

    // Then
    LocalDate upperBoundMonth = LocalDate.now().withDayOfMonth(1).plusMonths(12);
    assertThat(overviewRepository.findAllByMonthBefore(upperBoundMonth)).hasSize(18);
    assertThat(permissionRepository.findAll()).hasSize(Authority.values().length);
    AdminUserIdentity identity =
        adminUserIdentityRepository.findByUsername(AdminService.ADMIN_USERNAME);
    assertThat(identity).isNotNull();
    assertThat(userRoleRepository.findAllByUserId(identity.getUserId())).hasSize(1);
  }
}
