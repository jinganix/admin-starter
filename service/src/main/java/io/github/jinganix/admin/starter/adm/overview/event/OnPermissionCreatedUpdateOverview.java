package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnPermissionCreated;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnPermissionCreatedUpdateOverview extends OnPermissionCreated {

  private final OverviewRepository overviewRepository;

  @Override
  public void permissionsCreated(List<Permission> permissions) {
    overviewRepository.incrementPermissionCreated(
        LocalDate.now().withDayOfMonth(1), permissions.size());
  }
}
