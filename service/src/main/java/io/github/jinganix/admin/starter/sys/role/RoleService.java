package io.github.jinganix.admin.starter.sys.role;

import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final PermissionRepository permissionRepository;

  private final RolePermissionRepository rolePermissionRepository;

  private final UidGenerator uidGenerator;

  public void createRolePermissions(Long roleId, List<Long> permissionIds, long millis) {
    List<Permission> permissions = permissionRepository.findAllById(permissionIds);
    List<RolePermission> userRoles =
        permissions.stream()
            .filter(x -> x.getType() != PermissionType.GROUP)
            .map(
                x ->
                    (RolePermission)
                        new RolePermission()
                            .setId(uidGenerator.nextUid())
                            .setRoleId(roleId)
                            .setPermissionId(x.getId())
                            .setCreatedAt(millis)
                            .setUpdatedAt(millis))
            .toList();
    rolePermissionRepository.saveAll(userRoles);
  }
}
