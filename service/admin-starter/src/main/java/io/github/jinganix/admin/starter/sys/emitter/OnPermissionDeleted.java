package io.github.jinganix.admin.starter.sys.emitter;

import java.util.List;

public abstract class OnPermissionDeleted extends AbstractEmitter {

  @Override
  public abstract void permissionDeleted(List<Long> ids);
}
