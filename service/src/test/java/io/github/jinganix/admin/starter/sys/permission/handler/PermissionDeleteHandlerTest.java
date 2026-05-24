package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteResponse;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionDeleteHandler")
class PermissionDeleteHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionDeleteHandler permissionDeleteHandler;

  @Autowired PermissionRepository permissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when missing permission ids")
  void shouldThrowApiExceptionWhenMissingPermissionIds() {
    // Given
    PermissionDeleteRequest request = new PermissionDeleteRequest(List.of(UID_1));

    // When / Then
    assertThatThrownBy(() -> permissionDeleteHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PERMISSION_NOT_FOUND));
  }

  @Test
  @DisplayName("should delete permissions when existing permission ids")
  void shouldDeletePermissionsWhenExistingPermissionIds() {
    // Given
    testHelper.insertEntities(permission(UID_1), permission(UID_2).setCode("/test/code-2"));
    PermissionDeleteRequest request = new PermissionDeleteRequest(List.of(UID_1, UID_2));

    // When
    PermissionDeleteResponse response = permissionDeleteHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new PermissionDeleteResponse());
    assertThat(permissionRepository.findAllById(List.of(UID_1, UID_2))).isEmpty();
  }
}
