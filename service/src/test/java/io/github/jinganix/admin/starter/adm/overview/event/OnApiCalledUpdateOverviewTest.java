package io.github.jinganix.admin.starter.adm.overview.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OnApiCalledUpdateOverview")
class OnApiCalledUpdateOverviewTest {

  @Mock private OverviewRepository overviewRepository;

  @InjectMocks private OnApiCalledUpdateOverview onApiCalledUpdateOverview;

  @Nested
  @DisplayName("apiCalled")
  class ApiCalled {

    @Test
    @DisplayName("Given GET method -> increments api get counter")
    void givenGetMethod() {
      onApiCalledUpdateOverview.apiCalled("GET", "/adm/overview");

      verify(overviewRepository).incrementApiGet(LocalDate.now().withDayOfMonth(1), 1);
    }

    @Test
    @DisplayName("Given POST method -> increments api post counter")
    void givenPostMethod() {
      onApiCalledUpdateOverview.apiCalled("POST", "/sys/auth/token");

      verify(overviewRepository).incrementApiPost(LocalDate.now().withDayOfMonth(1), 1);
    }

    @Test
    @DisplayName("Given other method -> does nothing")
    void givenOtherMethod() {
      onApiCalledUpdateOverview.apiCalled("PUT", "/sys/user");

      assertThat(onApiCalledUpdateOverview).isNotNull();
    }
  }
}
