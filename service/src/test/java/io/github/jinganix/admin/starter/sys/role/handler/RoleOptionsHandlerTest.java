package io.github.jinganix.admin.starter.sys.role.handler;

import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.lib.option.OptionStringPb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsResponse;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("RoleOptionsHandler")
class RoleOptionsHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired RoleOptionsHandler roleOptionsHandler;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return empty options when only inactive roles")
  void shouldReturnEmptyOptionsWhenOnlyInactiveRoles() {
    // Given
    testHelper.insertEntities(role(UID_1).setStatus(RoleStatus.INACTIVE));

    // When
    RoleOptionsResponse response = roleOptionsHandler.handle();

    // Then
    assertThat(response).usingRecursiveComparison().isEqualTo(new RoleOptionsResponse(emptyList()));
  }

  @Test
  @DisplayName("should return role option when active role")
  void shouldReturnRoleOptionWhenActiveRole() {
    // Given
    testHelper.insertEntities(role(UID_1));

    // When
    RoleOptionsResponse response = roleOptionsHandler.handle();

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new RoleOptionsResponse(List.of(new OptionStringPb("Role 10001", UID_1 + ""))));
  }
}
