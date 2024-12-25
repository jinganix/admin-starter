package io.github.jinganix.admin.starter.tests;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Start a mysql docker container. */
public class MysqlContainer extends MySQLContainer<MysqlContainer> {

  private static final String VERSION = "9.1.0";

  /** Constructor. */
  public MysqlContainer() {
    super(
        DockerImageName.parse((isArm64() ? "arm64v8/mysql:" : "mysql:") + VERSION)
            .asCompatibleSubstituteFor("mysql"));
  }

  private static boolean isArm64() {
    return System.getProperty("os.arch").equals("aarch64");
  }
}
