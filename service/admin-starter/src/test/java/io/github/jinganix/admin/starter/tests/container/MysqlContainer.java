package io.github.jinganix.admin.starter.tests.container;

import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Start a redis docker container. */
public class MysqlContainer extends MySQLContainer {

  private static final String VERSION = "9.6.0";

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
