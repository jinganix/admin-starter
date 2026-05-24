package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionType;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionCreateHandler")
class PermissionCreateHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionCreateHandler permissionCreateHandler;

  @Autowired PermissionRepository permissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given duplicate code -> throw ApiException")
  void givenDuplicateCode() {
    // Given
    String code = "/test/duplicate";
    testHelper.insertEntities(permission(UID_1).setCode(code));
    PermissionCreateRequest request =
        (PermissionCreateRequest)
            new PermissionCreateRequest()
                .setName("duplicate-perm")
                .setCode(code)
                .setType(PermissionType.API)
                .setDescription("")
                .setStatus(PermissionStatus.ACTIVE);

    // When / Then
    assertThatThrownBy(() -> permissionCreateHandler.handle(request))
        .isInstanceOf(ApiException.class)
        .satisfies(
            error ->
                assertThat(((ApiException) error).getCode())
                    .isEqualTo(ErrorCode.PERMISSION_EXISTS));
  }

  @Test
  @DisplayName("Given valid request -> create permission")
  void givenValidRequest() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    PermissionCreateRequest request =
        (PermissionCreateRequest)
            new PermissionCreateRequest()
                .setName("new-permission")
                .setCode("/test/new")
                .setType(PermissionType.API)
                .setDescription("desc")
                .setStatus(PermissionStatus.ACTIVE);

    // When
    PermissionCreateResponse response = permissionCreateHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new PermissionCreateResponse());
    Permission saved = permissionRepository.findById(UID_1);
    assertThat(saved)
        .usingRecursiveComparison()
        .isEqualTo(
            permission(UID_1)
                .setName("new-permission")
                .setCode("/test/new")
                .setDescription("desc")
                .setCreatedAt(MILLIS)
                .setUpdatedAt(MILLIS));
  }
}
