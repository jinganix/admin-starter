package io.github.jinganix.admin.starter.sys.role.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RolePermission extends AbstractEntity {

  private Long id;

  private Long roleId;

  private Long permissionId;
}
