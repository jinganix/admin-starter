package io.github.jinganix.admin.starter.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AuthedUser")
class AuthedUserTest {

  @Test
  @DisplayName("Given id -> getter and setter should round-trip")
  void givenIdShouldRoundTrip() {
    AuthedUser user = new AuthedUser().setId(100L);

    assertThat(user.getId()).isEqualTo(100L);
  }
}
