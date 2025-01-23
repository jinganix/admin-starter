package io.github.jinganix.admin.starter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mockStatic;

import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("AdminStarterApplication")
class AdminStarterApplicationTest extends SpringBootIntegrationTests {

  @Nested
  @DisplayName("when load context")
  class WhenLoadContext {

    @Test
    @DisplayName("then loaded")
    void thenLoaded() {
      assertThatCode(AdminStarterApplicationTest::new).doesNotThrowAnyException();
      try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
        assertThat(application).isNotNull();
        assertThatCode(() -> AdminStarterApplication.main(null)).doesNotThrowAnyException();
      }
    }
  }
}
