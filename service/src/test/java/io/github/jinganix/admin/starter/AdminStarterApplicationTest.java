package io.github.jinganix.admin.starter;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mockStatic;

import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

@DisplayName("AdminStarterApplication")
class AdminStarterApplicationTest extends SpringBootIntegrationTests {

  @Test
  @DisplayName("should application runs when startup")
  void shouldApplicationRunsWhenStartup() {
    // Given
    try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
      // When / Then
      assertThatCode(() -> AdminStarterApplication.main(null)).doesNotThrowAnyException();
    }
  }
}
