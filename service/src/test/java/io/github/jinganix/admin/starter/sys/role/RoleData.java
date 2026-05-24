package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.proto.sys.role.RolePb;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import java.util.Collections;
import java.util.List;

public class RoleData {

  public static Role role(long id) {
    return (Role)
        new Role()
            .setId(id)
            .setCode("role-" + id)
            .setName("Role " + id)
            .setDescription("role description")
            .setStatus(RoleStatus.ACTIVE)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }

  public static RolePb rolePb(long id) {
    return rolePb(id, Collections.emptyList());
  }

  public static RolePb rolePb(long id, List<Long> permissionIds) {
    RolePb pb = new RolePb();
    pb.setId(id);
    pb.setCode("role-" + id);
    pb.setName("Role " + id);
    pb.setDescription("role description");
    pb.setStatus(io.github.jinganix.admin.starter.proto.sys.role.RoleStatus.ACTIVE);
    pb.setCreatedAt(MIN_TIMESTAMP);
    pb.setPermissionIds(permissionIds);
    return pb;
  }

  public static RolePermission rolePermission(long id, long roleId, long permissionId) {
    return (RolePermission)
        new RolePermission()
            .setId(id)
            .setRoleId(roleId)
            .setPermissionId(permissionId)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
