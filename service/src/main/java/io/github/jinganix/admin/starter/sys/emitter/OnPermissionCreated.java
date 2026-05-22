package io.github.jinganix.admin.starter.sys.emitter;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import java.util.List;

public abstract class OnPermissionCreated extends AbstractEmitter {

  @Override
  public abstract void permissionsCreated(List<Permission> permissions);
}
