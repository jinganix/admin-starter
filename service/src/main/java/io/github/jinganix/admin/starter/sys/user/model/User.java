package io.github.jinganix.admin.starter.sys.user.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class User extends AbstractEntity {

  private Long id;

  private String nickname;

  private UserStatus status;
}
