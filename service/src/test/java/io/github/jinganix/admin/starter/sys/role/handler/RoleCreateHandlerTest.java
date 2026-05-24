package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
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
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleCreateHandler")
class RoleCreateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleCreateHandler roleCreateHandler;

  @Autowired RoleRepository roleRepository;

  @Autowired RolePermissionRepository rolePermissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when duplicate role code")
  void shouldThrowApiExceptionWhenDuplicateRoleCode() {
    // Given
    testHelper.insertEntities(role(UID_1).setCode("dup-code"));
    RoleCreateRequest request =
        (RoleCreateRequest)
            new RoleCreateRequest()
                .setCode("dup-code")
                .setName("new-role")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(Collections.emptyList());

    // When / Then
    assertThatThrownBy(() -> roleCreateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error -> assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.ROLE_EXISTS));
  }

  @Test
  @DisplayName("should create role when valid request")
  void shouldCreateRoleWhenValidRequest() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    RoleCreateRequest request =
        (RoleCreateRequest)
            new RoleCreateRequest()
                .setCode("new-code")
                .setName("new-role")
                .setDescription("new description")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(Collections.emptyList());

    // When
    RoleCreateResponse response = roleCreateHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new RoleCreateResponse());
    Role role = roleRepository.findById(UID_1);
    assertThat(role)
        .usingRecursiveComparison()
        .isEqualTo(
            role(UID_1)
                .setCode("new-code")
                .setName("new-role")
                .setDescription("new description")
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));
  }

  @Test
  @DisplayName("should create role permissions excludes group permission when group permission id")
  void shouldCreateRolePermissionsExcludesGroupPermissionWhenGroupPermissionId() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_4);
    testHelper.insertEntities(
        permission(UID_2).setCode("perm-api").setType(PermissionType.API),
        permission(UID_3).setCode("perm-group").setType(PermissionType.GROUP));
    RoleCreateRequest request =
        (RoleCreateRequest)
            new RoleCreateRequest()
                .setCode("new-code")
                .setName("new-role")
                .setStatus(RoleStatus.ACTIVE)
                .setPermissionIds(List.of(UID_2, UID_3));

    // When
    roleCreateHandler.handle(request);

    // Then
    assertThat(rolePermissionRepository.findByRoleId(UID_1))
        .extracting(x -> x.getPermissionId())
        .containsExactly(UID_2);
  }
}
