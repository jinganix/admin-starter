package io.github.jinganix.admin.starter.sys.user;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.proto.sys.user.UserDetailsPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.auth.model.AuthProvider;
import java.util.List;

public class UserData {

  public static UserDetailsPb userDetailsPb(
      long id, String username, String nickname, List<Long> roleIds) {
    UserDetailsPb pb = new UserDetailsPb();
    pb.setId(id);
    pb.setUsername(username);
    pb.setNickname(nickname);
    pb.setStatus(UserStatus.ACTIVE);
    pb.setCreatedAt(MIN_TIMESTAMP);
    pb.setRoleIds(roleIds);
    return pb;
  }

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
