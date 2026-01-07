package io.github.jinganix.admin.starter.tests;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/** Start a redis docker container. */
public class RedisContainer extends GenericContainer<RedisContainer> {

  private static final String VERSION = "8.2.1-alpine";

  /** REDIS_PORT. */
  public static final Integer REDIS_PORT = 6379;

  /** Constructor. */
  public RedisContainer() {
    super(DockerImageName.parse((isArm64() ? "arm64v8/redis:" : "redis:") + VERSION));
    this.addExposedPort(REDIS_PORT);
  }

  private static boolean isArm64() {
    return System.getProperty("os.arch").equals("aarch64");
  }

  /** Start a container. */
  @Override
  public void start() {
    super.start();
    System.setProperty("spring.data.redis.url", getUrl());
  }

  private String getUrl() {
    return "redis://" + this.getHost() + ":" + getMappedPort(REDIS_PORT);
  }
}
