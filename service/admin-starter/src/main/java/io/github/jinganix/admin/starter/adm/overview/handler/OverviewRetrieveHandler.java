package io.github.jinganix.admin.starter.adm.overview.handler;

import io.github.jinganix.admin.starter.adm.overview.OverviewMapper;
import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverviewRetrieveHandler {

  private final OverviewMapper overviewMapper;

  private final OverviewRepository overviewRepository;

  public OverviewListResponse handle() {
    List<Overview> overviews =
        overviewRepository.findAllByMonthBefore(LocalDate.now().withDayOfMonth(2));
    return new OverviewListResponse(overviews.stream().map(overviewMapper::overviewPb).toList());
  }
}
