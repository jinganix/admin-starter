package io.github.jinganix.admin.starter.sys.user;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;

public class UserData {

  public static UserCredential userCredential(long id) {
    return (UserCredential)
        new UserCredential()
            .setId(id)
            .setUsername("user-" + id)
            .setPassword("pwd-" + id)
            .setUpdatedAt(MIN_TIMESTAMP)
            .setCreatedAt(MIN_TIMESTAMP);
  }
}
