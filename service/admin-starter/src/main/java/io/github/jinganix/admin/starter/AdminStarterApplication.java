package io.github.jinganix.admin.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Application. */
@EnableCaching
@EnableTransactionManagement
@EnableConfigurationProperties
@SpringBootApplication(exclude = TaskExecutionAutoConfiguration.class)
public class AdminStarterApplication {

  /**
   * The main method.
   *
   * @param args arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(AdminStarterApplication.class, args);
  }
}
