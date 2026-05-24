package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permissionPb;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionRetrieveHandler")
class PermissionRetrieveHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionRetrieveHandler permissionRetrieveHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should throw ApiException when permission not found")
  void shouldThrowApiExceptionWhenPermissionNotFound() {
    // Given
    PermissionRetrieveRequest request = new PermissionRetrieveRequest(UID_3);

    // When / Then
    assertThatThrownBy(() -> permissionRetrieveHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PERMISSION_NOT_FOUND));
  }

  @Test
  @DisplayName("should return permission response when existing permission")
  void shouldReturnPermissionResponseWhenExistingPermission() {
    // Given
    testHelper.insertEntities(permission(UID_3));
    PermissionRetrieveRequest request = new PermissionRetrieveRequest(UID_3);

    // When
    PermissionRetrieveResponse response = permissionRetrieveHandler.handle(request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new PermissionRetrieveResponse(permissionPb(UID_3)));
  }
}
