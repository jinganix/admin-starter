package io.github.jinganix.admin.starter.sys.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithUsername {

  private User user;

  private String username;
}
