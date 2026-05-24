package io.github.jinganix.admin.starter.helper.auth.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseUserInactiveChecker")
class DatabaseUserInactiveCheckerTest {

  @Mock private UserRepository userRepository;

  private DatabaseUserInactiveChecker databaseUserInactiveChecker;

  @BeforeEach
  void setup() {
    databaseUserInactiveChecker = new DatabaseUserInactiveChecker(userRepository);
  }

  @Test
  @DisplayName("should return true when missing user")
  void shouldReturnTrueWhenMissingUser() {
    when(userRepository.findById(1L)).thenReturn(null);

    assertThat(databaseUserInactiveChecker.isInactive(1L)).isTrue();
  }

  @Test
  @DisplayName("should return true when inactive user")
  void shouldReturnTrueWhenInactiveUser() {
    when(userRepository.findById(1L)).thenReturn(new User().setStatus(UserStatus.INACTIVE));

    assertThat(databaseUserInactiveChecker.isInactive(1L)).isTrue();
  }

  @Test
  @DisplayName("should return false when active user")
  void shouldReturnFalseWhenActiveUser() {
    when(userRepository.findById(1L)).thenReturn(new User().setStatus(UserStatus.ACTIVE));

    assertThat(databaseUserInactiveChecker.isInactive(1L)).isFalse();
  }
}
