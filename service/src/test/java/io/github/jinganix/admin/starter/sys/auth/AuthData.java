package io.github.jinganix.admin.starter.sys.auth;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
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

  public static AdminUserToken userToken(long userId) {
    return new AdminUserToken().setUserId(userId).setRefreshToken("").setCreatedAt(MIN_TIMESTAMP);
  }
}
