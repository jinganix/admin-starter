package io.github.jinganix.admin.starter.sys.auth;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.sys.auth.model.UserToken;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;

public class AuthData {

  public static User user(long id) {
    return (User)
        new User()
            .setId(id)
            .setStatus(UserStatus.ACTIVE)
            .setUpdatedAt(MIN_TIMESTAMP)
            .setCreatedAt(MIN_TIMESTAMP);
  }

  public static UserToken userToken(long id) {
    return (UserToken)
        new UserToken()
            .setId(id)
            .setUserId(id)
            .setRefreshToken("")
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
