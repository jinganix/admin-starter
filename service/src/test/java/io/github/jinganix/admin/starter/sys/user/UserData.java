package io.github.jinganix.admin.starter.sys.user;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.auth.model.AuthProvider;

public class UserData {

  public static AdminUserIdentity userIdentity(long userId) {
    return (AdminUserIdentity)
        new AdminUserIdentity()
            .setId(userId)
            .setUserId(userId)
            .setProvider(AuthProvider.USERNAME)
            .setUsername("user-" + userId)
            .setPassword("pwd-" + userId)
            .setVerified(true)
            .setUpdatedAt(MIN_TIMESTAMP)
            .setCreatedAt(MIN_TIMESTAMP);
  }
}
