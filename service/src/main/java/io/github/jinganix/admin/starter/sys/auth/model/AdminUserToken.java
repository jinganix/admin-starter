package io.github.jinganix.admin.starter.sys.auth.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUserToken {

  private Long userId;

  private String refreshToken;

  private Long createdAt;
}
