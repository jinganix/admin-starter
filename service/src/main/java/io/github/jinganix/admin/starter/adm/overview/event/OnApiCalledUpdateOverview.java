package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnApiCalled;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnApiCalledUpdateOverview extends OnApiCalled {

  private final OverviewRepository overviewRepository;

  @Override
  public void apiCalled(String method, String path) {
    if ("GET".equals(method)) {
      overviewRepository.incrementApiGet(LocalDate.now().withDayOfMonth(1), 1);
    } else if ("POST".equals(method)) {
      overviewRepository.incrementApiPost(LocalDate.now().withDayOfMonth(1), 1);
    }
  }
}
