package io.github.jinganix.admin.starter.helper.auth.token;

import io.github.jinganix.admin.starter.helper.auth.UserInactiveChecker;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseUserInactiveChecker implements UserInactiveChecker {

  private final UserRepository userRepository;

  @Override
  public boolean isInactive(Long userId) {
    User user = userRepository.findById(userId).orElse(null);
    return user == null || user.getStatus() != UserStatus.ACTIVE;
  }
}
