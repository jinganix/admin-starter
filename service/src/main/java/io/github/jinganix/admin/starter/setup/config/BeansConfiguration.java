package io.github.jinganix.admin.starter.setup.config;

import io.github.jinganix.admin.starter.helper.uid.SnowflakeGenerator;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.peashooter.Tracer;
import io.github.jinganix.peashooter.trace.DefaultTracer;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestBodyAdvice;
import io.github.jinganix.webpb.runtime.reactive.WebpbClient;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.reactive.function.client.WebClient;

/** Configuration for beans. */
@Configuration
@RequiredArgsConstructor
public class BeansConfiguration {

  @Bean
  UidGenerator uidGenerator() {
    return new SnowflakeGenerator();
  }

  @Bean
  WebpbRequestBodyAdvice requestBodyAdvice() {
    return new WebpbRequestBodyAdvice();
  }

  @Bean
  WebpbClient webpbClient() {
    return new WebpbClient(WebClient.builder().build());
  }

  @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
  AsyncTaskExecutor asyncTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return x -> x.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  Tracer tracer() {
    return new DefaultTracer();
  }
}
