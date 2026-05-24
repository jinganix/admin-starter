package io.github.jinganix.admin.starter.sys.permission.handler;

import static io.github.jinganix.admin.starter.sys.permission.PermissionData.permission;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsResponse;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("PermissionOptionsHandler")
class PermissionOptionsHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired PermissionOptionsHandler permissionOptionsHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return empty options when only inactive permissions")
  void shouldReturnEmptyOptionsWhenOnlyInactivePermissions() {
    // Given
    testHelper.insertEntities(
        permission(UID_1).setStatus(PermissionStatus.INACTIVE),
        permission(UID_2).setCode("/test/code-2").setStatus(PermissionStatus.INACTIVE));

    // When
    PermissionOptionsResponse response = permissionOptionsHandler.handle();

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new PermissionOptionsResponse(emptyList()));
  }

  @Test
  @DisplayName("should return options when active permissions")
  void shouldReturnOptionsWhenActivePermissions() {
    // Given
    testHelper.insertEntities(
        permission(UID_1).setName("perm-one").setCode("/test/one"),
        permission(UID_2).setName("perm-two").setCode("/test/two"));

    // When
    PermissionOptionsResponse response = permissionOptionsHandler.handle();

    // Then
    assertThat(response.getOptions())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (PermissionOptionPb)
                new PermissionOptionPb()
                    .setCode("/test/one")
                    .setLabel("perm-one")
                    .setValue(UID_1 + ""),
            (PermissionOptionPb)
                new PermissionOptionPb()
                    .setCode("/test/two")
                    .setLabel("perm-two")
                    .setValue(UID_2 + ""));
  }
}
