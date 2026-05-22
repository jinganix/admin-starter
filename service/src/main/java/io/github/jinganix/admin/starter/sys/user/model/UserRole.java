package io.github.jinganix.admin.starter.sys.user.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserRole extends AbstractEntity {

  private Long id;

  private Long userId;

  private Long roleId;
}
