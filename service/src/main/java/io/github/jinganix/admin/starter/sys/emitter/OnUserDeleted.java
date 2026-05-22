package io.github.jinganix.admin.starter.sys.emitter;

import java.util.List;

public abstract class OnUserDeleted extends AbstractEmitter {

  @Override
  public abstract void userDeleted(List<Long> ids);
}
