package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePb;
import static io.github.jinganix.admin.starter.sys.role.RoleData.rolePermission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleRetrieveHandler")
class RoleRetrieveHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleRetrieveHandler roleRetrieveHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when role not found")
  void shouldThrowApiExceptionWhenRoleNotFound() {
    // Given
    RoleRetrieveRequest request = new RoleRetrieveRequest(UID_1);

    // When / Then
    assertThatThrownBy(() -> roleRetrieveHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND));
  }

  @Test
  @DisplayName("should return role response when existing role")
  void shouldReturnRoleResponseWhenExistingRole() {
    // Given
    testHelper.insertEntities(role(UID_1), rolePermission(UID_2, UID_1, UID_3));
    RoleRetrieveRequest request = new RoleRetrieveRequest(UID_1);

    // When
    RoleRetrieveResponse response = roleRetrieveHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new RoleRetrieveResponse(rolePb(UID_1, List.of(UID_3))));
  }
}
