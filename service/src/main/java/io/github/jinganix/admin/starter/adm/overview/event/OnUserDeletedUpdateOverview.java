package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnUserDeleted;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnUserDeletedUpdateOverview extends OnUserDeleted {

  private final OverviewRepository overviewRepository;

  @Override
  public void userDeleted(List<Long> ids) {
    overviewRepository.incrementUserDeleted(LocalDate.now().withDayOfMonth(1), ids.size());
  }
}
