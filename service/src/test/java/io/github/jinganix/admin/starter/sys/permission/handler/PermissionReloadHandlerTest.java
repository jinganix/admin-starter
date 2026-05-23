package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.Authority.SYS_PERMISSION_CREATE;
import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadResponse;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionReloadHandler")
class PermissionReloadHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionReloadHandler permissionReloadHandler;

  @Autowired PermissionRepository permissionRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given all authorities already exist -> skip duplicates")
  void givenAllAuthoritiesAlreadyExist() {
    // Given
    long id = UID_1;
    for (Authority authority : Authority.values()) {
      String code = authority.getValue();
      PermissionType type = code.endsWith("/") ? PermissionType.GROUP : PermissionType.API;
      testHelper.insertEntities(
          permission(id++)
              .setCode(code)
              .setName("authority" + code.replace("/", "."))
              .setDescription("")
              .setType(type));
    }

    // When
    PermissionReloadResponse response = permissionReloadHandler.handle();

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new PermissionReloadResponse());
    assertThat(permissionRepository.findAll()).hasSize(Authority.values().length);
  }

  @Test
  @DisplayName("Given empty database -> reload all authorities")
  void givenEmptyDatabase() {
    // Given

    // When
    PermissionReloadResponse response = permissionReloadHandler.handle();

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new PermissionReloadResponse());
    assertThat(permissionRepository.findAll()).hasSize(Authority.values().length);
    assertThat(permissionRepository.existsByCode(SYS_PERMISSION_CREATE.getValue())).isTrue();
  }
}
