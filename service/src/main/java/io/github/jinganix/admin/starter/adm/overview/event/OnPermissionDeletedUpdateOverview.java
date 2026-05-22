package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnPermissionDeleted;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnPermissionDeletedUpdateOverview extends OnPermissionDeleted {

  private final OverviewRepository overviewRepository;

  @Override
  public void permissionDeleted(List<Long> ids) {
    overviewRepository.incrementPermissionDeleted(LocalDate.now().withDayOfMonth(1), ids.size());
  }
}
