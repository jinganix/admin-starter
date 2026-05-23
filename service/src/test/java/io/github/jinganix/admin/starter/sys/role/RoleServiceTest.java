package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleService")
class RoleServiceTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleService roleService;

  @Autowired RolePermissionRepository rolePermissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given group permission ids -> ignore group type when create role permissions")
  void givenGroupPermissionIds() {
    // Given
    long roleId = UID_4;
    testHelper.insertEntities(
        permission(UID_1).setCode("perm-group").setType(PermissionType.GROUP),
        permission(UID_2).setCode("perm-api").setType(PermissionType.API),
        permission(UID_3).setCode("perm-ui").setType(PermissionType.UI));
    when(uidGenerator.nextUid()).thenReturn(1001L, 1002L);

    // When
    roleService.createRolePermissions(roleId, List.of(UID_1, UID_2, UID_3), MILLIS);

    // Then
    assertThat(rolePermissionRepository.findByRoleId(roleId))
        .extracting(RolePermission::getPermissionId)
        .containsExactlyInAnyOrder(UID_2, UID_3);
  }
}
