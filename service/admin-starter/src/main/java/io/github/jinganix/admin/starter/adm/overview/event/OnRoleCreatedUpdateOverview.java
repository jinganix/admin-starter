package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnRoleCreated;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnRoleCreatedUpdateOverview extends OnRoleCreated {

  private final OverviewRepository overviewRepository;

  @Override
  public void roleCreated(Role role) {
    overviewRepository.incrementRoleCreated(LocalDate.now().withDayOfMonth(1), 1);
  }
}
