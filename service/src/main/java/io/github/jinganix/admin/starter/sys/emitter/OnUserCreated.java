package io.github.jinganix.admin.starter.sys.emitter;

import io.github.jinganix.admin.starter.sys.user.model.User;

public abstract class OnUserCreated extends AbstractEmitter {

  @Override
  public abstract void userCreated(User user);
}
