package io.github.jinganix.admin.starter.sys.emitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AbstractEmitter")
class AbstractEmitterTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should default methods are no-op when base abstract emitter")
  void shouldDefaultMethodsAreNoOpWhenBaseAbstractEmitter() {
    // Given
    AbstractEmitter emitter = new AbstractEmitter() {};
    Role role = new Role().setCode("ROLE");
    User user = new User().setNickname("tester");
    Permission permission = new Permission().setCode("PERM");

    // When / Then
    assertThat(emitter.order()).isZero();
    assertThatCode(() -> emitter.apiCalled("GET", "/path")).doesNotThrowAnyException();
    assertThatCode(() -> emitter.userCreated(user)).doesNotThrowAnyException();
    assertThatCode(() -> emitter.userDeleted(List.of(1L))).doesNotThrowAnyException();
    assertThatCode(() -> emitter.roleCreated(role)).doesNotThrowAnyException();
    assertThatCode(() -> emitter.roleDeleted(List.of(2L))).doesNotThrowAnyException();
    assertThatCode(() -> emitter.permissionsCreated(List.of(permission)))
        .doesNotThrowAnyException();
    assertThatCode(() -> emitter.permissionDeleted(List.of(3L))).doesNotThrowAnyException();
  }
}
