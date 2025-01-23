package io.github.jinganix.admin.starter.sys.emitter;

import java.util.List;

public abstract class OnRoleDeleted extends AbstractEmitter {

  @Override
  public abstract void roleDeleted(List<Long> ids);
}
