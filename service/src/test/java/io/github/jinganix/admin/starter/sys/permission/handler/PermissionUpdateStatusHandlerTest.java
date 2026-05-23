package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionUpdateStatusHandler")
class PermissionUpdateStatusHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionUpdateStatusHandler permissionUpdateStatusHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given permission not found -> throw ApiException")
  void givenPermissionNotFound() {
    // Given
    PermissionUpdateStatusRequest request =
        new PermissionUpdateStatusRequest(UID_3, PermissionStatus.INACTIVE);

    // When / Then
    assertThatThrownBy(() -> permissionUpdateStatusHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PERMISSION_NOT_FOUND));
  }

  @Test
  @DisplayName("Given existing permission -> return updated status response")
  void givenExistingPermission() {
    // Given
    testHelper.insertEntities(permission(UID_3));
    PermissionUpdateStatusRequest request =
        new PermissionUpdateStatusRequest(UID_3, PermissionStatus.INACTIVE);

    // When
    PermissionUpdateStatusResponse response = permissionUpdateStatusHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new PermissionUpdateStatusResponse(UID_3, PermissionStatus.INACTIVE));
  }
}
