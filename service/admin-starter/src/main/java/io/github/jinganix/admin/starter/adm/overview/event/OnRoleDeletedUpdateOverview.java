package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnRoleDeleted;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnRoleDeletedUpdateOverview extends OnRoleDeleted {

  private final OverviewRepository overviewRepository;

  @Override
  public void roleDeleted(List<Long> ids) {
    overviewRepository.incrementRoleDeleted(LocalDate.now().withDayOfMonth(1), ids.size());
  }
}
