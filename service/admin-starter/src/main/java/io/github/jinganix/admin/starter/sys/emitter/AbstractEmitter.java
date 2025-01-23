package io.github.jinganix.admin.starter.sys.emitter;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.user.model.User;
import java.util.List;

abstract class AbstractEmitter {

  public int order() {
    return 0;
  }

  void apiCalled(String method, String path) {}

  void userCreated(User user) {}

  void userDeleted(List<Long> ids) {}

  void roleCreated(Role role) {}

  void roleDeleted(List<Long> ids) {}

  void permissionsCreated(List<Permission> permissions) {}

  void permissionDeleted(List<Long> ids) {}
}
