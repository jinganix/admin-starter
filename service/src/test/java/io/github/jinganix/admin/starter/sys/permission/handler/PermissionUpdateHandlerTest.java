package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.CODE;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permissionPb;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.updateRequest;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionType;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionUpdateHandler")
class PermissionUpdateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionUpdateHandler permissionUpdateHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  private static PermissionPb expectedPermissionPb(long id) {
    PermissionPb pb = permissionPb(id);
    pb.setName("updated-name");
    pb.setCode(CODE);
    pb.setDescription("updated-description");
    pb.setType(PermissionType.UI);
    pb.setStatus(PermissionStatus.INACTIVE);
    return pb;
  }

  @Test
  @DisplayName("Given permission not found -> throw ApiException")
  void givenPermissionNotFound() {
    // Given
    PermissionUpdateRequest request = updateRequest(UID_3);

    // When / Then
    assertThatThrownBy(() -> permissionUpdateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PERMISSION_NOT_FOUND));
  }

  @Test
  @DisplayName("Given existing permission -> return updated permission response")
  void givenExistingPermission() {
    // Given
    testHelper.insertEntities(permission(UID_3));
    PermissionUpdateRequest request = updateRequest(UID_3);
    request.setName("updated-name");
    request.setDescription("updated-description");
    request.setType(PermissionType.UI);
    request.setStatus(PermissionStatus.INACTIVE);

    // When
    PermissionUpdateResponse response = permissionUpdateHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new PermissionUpdateResponse(expectedPermissionPb(UID_3)));
  }
}
