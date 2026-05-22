package io.github.jinganix.admin.starter.sys.utils;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.sys.permission.PermissionService;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {

  private final AdminService adminService;

  private final OverviewRepository overviewRepository;

  private final PermissionService permissionService;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  @Transactional
  @PostConstruct
  void initialize() {
    long millis = utilsService.currentTimeMillis();
    initOverviewData(millis);
    initPermissionData(millis);
    adminService.initAdminData(millis);
  }

  private void initPermissionData(long millis) {
    permissionService.reload(millis);
  }

  private void initOverviewData(long millis) {
    LocalDate today = LocalDate.now();
    for (int i = -6; i < 12; i++) {
      LocalDate month = today.withDayOfMonth(1).plusMonths(i);
      if (!overviewRepository.existsByMonth(month)) {
        overviewRepository.save(
            (Overview)
                new Overview()
                    .setId(uidGenerator.nextUid())
                    .setMonth(month)
                    .setCreatedAt(millis)
                    .setUpdatedAt(millis));
      }
    }
  }
}
