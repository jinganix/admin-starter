package io.github.jinganix.admin.starter.sys.role.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "sys_role_permission")
public class RolePermission extends AbstractEntity {

  @Id private Long id;

  private Long roleId;

  private Long permissionId;
}
