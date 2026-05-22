package io.github.jinganix.admin.starter.sys.emitter;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.user.model.User;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class Emitter extends AbstractEmitter {

  private final List<OnApiCalled> apiCalled;

  private final List<OnPermissionCreated> permissionCreated;

  private final List<OnPermissionDeleted> permissionDeleted;

  private final List<OnRoleCreated> roleCreated;

  private final List<OnRoleDeleted> roleDeleted;

  private final List<OnUserCreated> userCreated;

  private final List<OnUserDeleted> userDeleted;

  @PostConstruct
  @SuppressWarnings("unchecked")
  void initialize() throws IllegalAccessException {
    for (Field field : this.getClass().getDeclaredFields()) {
      if (!List.class.isAssignableFrom(field.getType())) {
        continue;
      }
      Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
      if (!AbstractEmitter.class.isAssignableFrom((Class<?>) type)) {
        continue;
      }
      List<AbstractEmitter> emitters = (List<AbstractEmitter>) field.get(this);
      emitters.sort(Comparator.comparingInt(AbstractEmitter::order));
    }
  }

  private <T extends AbstractEmitter> void emit(List<T> emitters, Consumer<T> func) {
    if (CollectionUtils.isEmpty(emitters)) {
      return;
    }
    for (T emitter : emitters) {
      func.accept(emitter);
    }
  }

  @Override
  @Transactional
  public void apiCalled(String method, String path) {
    this.emit(apiCalled, emitter -> emitter.apiCalled(method, path));
  }

  @Override
  @Transactional
  public void permissionsCreated(List<Permission> permissions) {
    this.emit(permissionCreated, emitter -> emitter.permissionsCreated(permissions));
  }

  @Override
  @Transactional
  public void permissionDeleted(List<Long> ids) {
    this.emit(permissionDeleted, emitter -> emitter.permissionDeleted(ids));
  }

  @Override
  @Transactional
  public void roleCreated(Role role) {
    this.emit(roleCreated, emitter -> emitter.roleCreated(role));
  }

  @Override
  @Transactional
  public void roleDeleted(List<Long> ids) {
    this.emit(roleDeleted, emitter -> emitter.roleDeleted(ids));
  }

  @Override
  @Transactional
  public void userCreated(User user) {
    this.emit(userCreated, emitter -> emitter.userCreated(user));
  }

  @Override
  @Transactional
  public void userDeleted(List<Long> ids) {
    this.emit(userDeleted, emitter -> emitter.userDeleted(ids));
  }
}
