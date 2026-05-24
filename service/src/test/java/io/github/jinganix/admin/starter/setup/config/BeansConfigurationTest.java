package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.helper.uid.SnowflakeGenerator;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.peashooter.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BeansConfiguration")
class BeansConfigurationTest {

  private final BeansConfiguration configuration = new BeansConfiguration();

  @Nested
  @DisplayName("uidGenerator")
  class UidGeneratorBean {

    @Test
    @DisplayName("Given configuration -> returns snowflake generator")
    void givenConfiguration() {
      UidGenerator uidGenerator = configuration.uidGenerator();

      assertThat(uidGenerator).isInstanceOf(SnowflakeGenerator.class);
    }
  }

  @Nested
  @DisplayName("tracer")
  class TracerBean {

    @Test
    @DisplayName("Given configuration -> returns tracer")
    void givenConfiguration() {
      Tracer tracer = configuration.tracer();

      assertThat(tracer).isNotNull();
    }
  }
}
