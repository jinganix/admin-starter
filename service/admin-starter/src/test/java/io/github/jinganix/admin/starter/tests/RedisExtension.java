package io.github.jinganix.admin.starter.tests;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Redis jupiter extension. */
public class RedisExtension implements BeforeAllCallback {

  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  private final RedisContainer container = new RedisContainer();

  /**
   * Start a container before all tests.
   *
   * @param context {@link ExtensionContext}
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    if (STARTED.compareAndSet(false, true)) {
      this.container.withReuse(true).start();
    }
  }
}
