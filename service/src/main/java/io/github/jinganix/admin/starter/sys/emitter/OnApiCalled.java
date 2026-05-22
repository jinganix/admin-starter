package io.github.jinganix.admin.starter.sys.emitter;

public abstract class OnApiCalled extends AbstractEmitter {

  @Override
  public abstract void apiCalled(String method, String path);
}
