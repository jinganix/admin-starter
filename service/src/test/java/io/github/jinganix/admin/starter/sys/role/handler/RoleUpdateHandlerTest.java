package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePb;
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
import io.github.jinganix.admin.starter.proto.sys.role.RolePb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleStatus;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleUpdateHandler")
class RoleUpdateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleUpdateHandler roleUpdateHandler;

  @Autowired AdminService adminService;

  @Autowired RoleRepository roleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given role not found -> throw ApiException")
  void givenRoleNotFound() {
    // Given
    RoleUpdateRequest request =
        (RoleUpdateRequest)
            new RoleUpdateRequest(UID_5)
                .setCode("updated-code")
                .setName("updated-name")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(Collections.emptyList());

    // When / Then
    assertThatThrownBy(() -> roleUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND));
  }

  @Test
  @DisplayName("Given admin role -> throw ApiException")
  void givenAdminRole() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    Role adminRole = roleRepository.findByCode(RoleCode.ADMIN.name());
    RoleUpdateRequest request =
        (RoleUpdateRequest)
            new RoleUpdateRequest(adminRole.getId())
                .setCode("updated-code")
                .setName("updated-name")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(Collections.emptyList());

    // When / Then
    assertThatThrownBy(() -> roleUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
  }

  @Test
  @DisplayName("Given existing role -> update role")
  void givenExistingRole() {
    // Given
    testHelper.insertEntities(role(UID_4).setCode("old-code").setName("Old Name"));
    RoleUpdateRequest request =
        (RoleUpdateRequest)
            new RoleUpdateRequest(UID_4)
                .setCode("new-code")
                .setName("New Name")
                .setDescription("updated description")
                .setStatus(RoleStatus.INACTIVE)
                .setPermissionIds(Collections.emptyList());

    // When
    RoleUpdateResponse response = roleUpdateHandler.handle(request);

    // Then
    RolePb expected = rolePb(UID_4, Collections.emptyList());
    expected.setCode("new-code");
    expected.setName("New Name");
    expected.setDescription("updated description");
    expected.setStatus(RoleStatus.INACTIVE);
    assertThat(response).usingRecursiveComparison().isEqualTo(new RoleUpdateResponse(expected));
    Role updated = roleRepository.findById(UID_4);
    assertThat(updated.getCode()).isEqualTo("new-code");
    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getUpdatedAt()).isEqualTo(MILLIS);
  }
}
