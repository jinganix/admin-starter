package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.helper.uid.SnowflakeGenerator;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.peashooter.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BeansConfiguration")
class BeansConfigurationTest {

  private final BeansConfiguration configuration = new BeansConfiguration();

  @Test
  @DisplayName("should return snowflake generator when configuration")
  void shouldReturnSnowflakeGeneratorWhenConfiguration() {
    UidGenerator uidGenerator = configuration.uidGenerator();

    assertThat(uidGenerator).isInstanceOf(SnowflakeGenerator.class);
  }

  @Test
  @DisplayName("should return tracer when configuration")
  void shouldReturnTracerWhenConfiguration() {
    Tracer tracer = configuration.tracer();

    assertThat(tracer).isNotNull();
  }
}
