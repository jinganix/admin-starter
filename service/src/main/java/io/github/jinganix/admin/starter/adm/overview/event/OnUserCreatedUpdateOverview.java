package io.github.jinganix.admin.starter.adm.overview.event;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.sys.emitter.OnUserCreated;
import io.github.jinganix.admin.starter.sys.user.model.User;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnUserCreatedUpdateOverview extends OnUserCreated {

  private final OverviewRepository overviewRepository;

  @Override
  public void userCreated(User user) {
    overviewRepository.incrementUserCreated(LocalDate.now().withDayOfMonth(1), 1);
  }
}
