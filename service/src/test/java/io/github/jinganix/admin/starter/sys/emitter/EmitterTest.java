package io.github.jinganix.admin.starter.sys.emitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Emitter")
class EmitterTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given mixed field types -> initialize sorts only abstract emitter lists")
  void givenMixedFieldTypesOnInitialize() throws IllegalAccessException {
    // Given
    RecordingOnApiCalled second = new RecordingOnApiCalled(2);
    RecordingOnApiCalled first = new RecordingOnApiCalled(1);
    InitializeCoverageEmitter emitter =
        new InitializeCoverageEmitter(new ArrayList<>(List.of(second, first)));

    // When
    emitter.initialize();

    // Then
    assertThat(emitter.sortedOrders()).containsExactly(1, 2);
  }

  @Nested
  @DisplayName("apiCalled")
  class ApiCalled {

    @Test
    @DisplayName("Given ordered emitters -> dispatch in ascending order")
    void givenOrderedEmitters() throws IllegalAccessException {
      // Given
      List<String> calls = new ArrayList<>();
      OrderedApiEmitter second = new OrderedApiEmitter(2, "second", calls);
      OrderedApiEmitter first = new OrderedApiEmitter(1, "first", calls);
      Emitter emitter =
          new Emitter(
              new ArrayList<>(List.of(second, first)),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>());
      emitter.initialize();

      // When
      emitter.apiCalled("post", "/api/path");

      // Then
      assertThat(calls).containsExactly("first:post:/api/path", "second:post:/api/path");
    }

    @Test
    @DisplayName("Given empty emitter list -> do nothing")
    void givenEmptyEmitterList() {
      // Given
      Emitter emitter =
          new Emitter(
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>());

      // When / Then
      assertThatCode(() -> emitter.apiCalled("GET", "/any")).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("permissionsCreated")
  class PermissionsCreated {

    @Test
    @DisplayName("Given permission emitters -> delegate call")
    void givenPermissionEmitters() {
      // Given
      Permission permission = new Permission().setCode("P");
      RecordingOnPermissionCreated recorder = new RecordingOnPermissionCreated();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(recorder), List.of(), List.of(), List.of(), List.of(), List.of());

      // When
      emitter.permissionsCreated(List.of(permission));

      // Then
      assertThat(recorder.permissions).containsExactly(permission);
    }
  }

  @Nested
  @DisplayName("permissionDeleted")
  class PermissionDeleted {

    @Test
    @DisplayName("Given permission deleted emitters -> delegate call")
    void givenPermissionDeletedEmitters() {
      // Given
      RecordingOnPermissionDeleted recorder = new RecordingOnPermissionDeleted();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(), List.of(recorder), List.of(), List.of(), List.of(), List.of());

      // When
      emitter.permissionDeleted(List.of(1L, 2L));

      // Then
      assertThat(recorder.ids).containsExactly(1L, 2L);
    }
  }

  @Nested
  @DisplayName("roleCreated")
  class RoleCreated {

    @Test
    @DisplayName("Given role created emitters -> delegate call")
    void givenRoleCreatedEmitters() {
      // Given
      Role role = new Role().setCode("ROLE");
      RecordingOnRoleCreated recorder = new RecordingOnRoleCreated();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(), List.of(), List.of(recorder), List.of(), List.of(), List.of());

      // When
      emitter.roleCreated(role);

      // Then
      assertThat(recorder.role).isEqualTo(role);
    }
  }

  @Nested
  @DisplayName("roleDeleted")
  class RoleDeleted {

    @Test
    @DisplayName("Given role deleted emitters -> delegate call")
    void givenRoleDeletedEmitters() {
      // Given
      RecordingOnRoleDeleted recorder = new RecordingOnRoleDeleted();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(), List.of(), List.of(), List.of(recorder), List.of(), List.of());

      // When
      emitter.roleDeleted(List.of(3L, 4L));

      // Then
      assertThat(recorder.ids).containsExactly(3L, 4L);
    }
  }

  @Nested
  @DisplayName("userCreated")
  class UserCreated {

    @Test
    @DisplayName("Given user created emitters -> delegate call")
    void givenUserCreatedEmitters() {
      // Given
      User user = new User().setNickname("tester");
      RecordingOnUserCreated recorder = new RecordingOnUserCreated();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(), List.of(), List.of(), List.of(), List.of(recorder), List.of());

      // When
      emitter.userCreated(user);

      // Then
      assertThat(recorder.user).isEqualTo(user);
    }
  }

  @Nested
  @DisplayName("userDeleted")
  class UserDeleted {

    @Test
    @DisplayName("Given user deleted emitters -> delegate call")
    void givenUserDeletedEmitters() {
      // Given
      RecordingOnUserDeleted recorder = new RecordingOnUserDeleted();
      Emitter emitter =
          new Emitter(
              List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(recorder));

      // When
      emitter.userDeleted(List.of(5L, 6L));

      // Then
      assertThat(recorder.ids).containsExactly(5L, 6L);
    }
  }

  private static class OrderedApiEmitter extends OnApiCalled {
    private final int order;
    private final String name;
    private final List<String> calls;

    private OrderedApiEmitter(int order, String name, List<String> calls) {
      this.order = order;
      this.name = name;
      this.calls = calls;
    }

    @Override
    public int order() {
      return order;
    }

    @Override
    public void apiCalled(String method, String path) {
      calls.add(name + ":" + method + ":" + path);
    }
  }

  private static class RecordingOnApiCalled extends OnApiCalled {
    private final int order;

    private RecordingOnApiCalled(int order) {
      this.order = order;
    }

    @Override
    public int order() {
      return order;
    }

    @Override
    public void apiCalled(String method, String path) {}
  }

  private static class RecordingOnPermissionCreated extends OnPermissionCreated {
    private List<Permission> permissions;

    @Override
    public void permissionsCreated(List<Permission> permissions) {
      this.permissions = permissions;
    }
  }

  private static class RecordingOnPermissionDeleted extends OnPermissionDeleted {
    private List<Long> ids;

    @Override
    public void permissionDeleted(List<Long> ids) {
      this.ids = ids;
    }
  }

  private static class RecordingOnRoleCreated extends OnRoleCreated {
    private Role role;

    @Override
    public void roleCreated(Role role) {
      this.role = role;
    }
  }

  private static class RecordingOnRoleDeleted extends OnRoleDeleted {
    private List<Long> ids;

    @Override
    public void roleDeleted(List<Long> ids) {
      this.ids = ids;
    }
  }

  private static class RecordingOnUserCreated extends OnUserCreated {
    private User user;

    @Override
    public void userCreated(User user) {
      this.user = user;
    }
  }

  private static class RecordingOnUserDeleted extends OnUserDeleted {
    private List<Long> ids;

    @Override
    public void userDeleted(List<Long> ids) {
      this.ids = ids;
    }
  }

  private static class InitializeCoverageEmitter extends Emitter {
    final String ignoredField = "ignored";
    final List<String> ignoredList = new ArrayList<>();
    final List<OnApiCalled> sortableApiCalled;

    private InitializeCoverageEmitter(List<OnApiCalled> sortableApiCalled) {
      super(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
      this.sortableApiCalled = sortableApiCalled;
    }

    private List<Integer> sortedOrders() {
      return sortableApiCalled.stream().map(AbstractEmitter::order).toList();
    }
  }
}
