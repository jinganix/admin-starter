package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.CODE;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.editPb;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionEditPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadResponse;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionUploadHandler")
class PermissionUploadHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionUploadHandler permissionUploadHandler;

  @Autowired PermissionRepository permissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given new permissions -> save permissions")
  void givenNewPermissions() {
    // Given
    when(uidGenerator.nextUid()).thenReturn(UID_3);
    PermissionEditPb pb = editPb().setCode("upload-code").setName("upload-name");
    PermissionUploadRequest request = new PermissionUploadRequest(List.of(pb));

    // When
    PermissionUploadResponse response = permissionUploadHandler.handle(request);

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new PermissionUploadResponse());
    Permission permission = permissionRepository.findById(UID_3);
    assertThat(permission.getCode()).isEqualTo("upload-code");
    assertThat(permission.getName()).isEqualTo("upload-name");
  }

  @Test
  @DisplayName("Given existing code -> skip duplicate permission")
  void givenExistingCode() {
    // Given
    testHelper.insertEntities(permission(UID_3));
    PermissionUploadRequest request = new PermissionUploadRequest(List.of(editPb()));

    // When
    permissionUploadHandler.handle(request);

    // Then
    assertThat(permissionRepository.findAll()).hasSize(1);
    assertThat(permissionRepository.findById(UID_3).getCode()).isEqualTo(CODE);
  }
}
