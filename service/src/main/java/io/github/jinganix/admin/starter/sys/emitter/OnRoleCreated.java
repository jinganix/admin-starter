package io.github.jinganix.admin.starter.sys.emitter;

import io.github.jinganix.admin.starter.sys.role.model.Role;

public abstract class OnRoleCreated extends AbstractEmitter {

  @Override
  public abstract void roleCreated(Role role);
}
