package io.github.jinganix.admin.starter.sys.permission.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Permission extends AbstractEntity {

  private Long id;

  private String code;

  private String name;

  private String description;

  private PermissionType type;

  private PermissionStatus status;
}
