package io.github.jinganix.admin.starter.tests;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Mysql jupiter extension. */
public class MysqlExtension implements BeforeAllCallback {

  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  private static final MysqlContainer container = new MysqlContainer();

  /**
   * Start a container before all tests.
   *
   * @param context {@link ExtensionContext}
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    if (!STARTED.compareAndSet(false, true)) {
      return;
    }
    container
        .withUsername("root")
        .withPassword("root")
        .withInitScript("init_test_container_database.sql")
        .withReuse(true)
        .start();
    System.setProperty("spring.datasource.url", container.getJdbcUrl());
  }
}
