package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
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
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleDeleteHandler")
class RoleDeleteHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleDeleteHandler roleDeleteHandler;

  @Autowired AdminService adminService;

  @Autowired RoleRepository roleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when role not found")
  void shouldThrowApiExceptionWhenRoleNotFound() {
    // Given
    RoleDeleteRequest request = new RoleDeleteRequest(List.of(UID_5));

    // When / Then
    assertThatThrownBy(() -> roleDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND));
  }

  @Test
  @DisplayName("should throw ApiException when partial existing roles")
  void shouldThrowApiExceptionWhenPartialExistingRoles() {
    // Given
    testHelper.insertEntities(role(UID_4));
    RoleDeleteRequest request = new RoleDeleteRequest(List.of(UID_4, UID_5));

    // When / Then
    assertThatThrownBy(() -> roleDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND));
  }

  @Test
  @DisplayName("should throw ApiException when admin role")
  void shouldThrowApiExceptionWhenAdminRole() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1, UID_2, UID_3, UID_4);
    adminService.initAdminData(MILLIS);
    Role adminRole = roleRepository.findByCode(RoleCode.ADMIN.name());
    RoleDeleteRequest request = new RoleDeleteRequest(List.of(adminRole.getId()));

    // When / Then
    assertThatThrownBy(() -> roleDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.ADMIN_IS_IMMUTABLE));
  }

  @Test
  @DisplayName("should delete role when existing role")
  void shouldDeleteRoleWhenExistingRole() {
    // Given
    testHelper.insertEntities(role(UID_4));
    RoleDeleteRequest request = new RoleDeleteRequest(List.of(UID_4));

    // When
    RoleDeleteResponse response = roleDeleteHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new RoleDeleteResponse());
    assertThat(roleRepository.findById(UID_4)).isNull();
  }
}
