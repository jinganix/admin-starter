package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.RoleService;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleUpdateHandler {

  private final AdminService adminService;

  private final RoleMapper roleMapper;

  private final RolePermissionRepository rolePermissionRepository;

  private final RoleRepository roleRepository;

  private final RoleService roleService;

  private final UtilsService utilsService;

  @Transactional
  public RoleUpdateResponse handle(RoleUpdateRequest request) {
    if (adminService.isAdminRole(request.getId())) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    Role role =
        roleRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.ROLE_NOT_FOUND));
    roleMapper.fill(role, request);
    long millis = utilsService.currentTimeMillis();
    roleRepository.save((Role) role.setUpdatedAt(millis));
    rolePermissionRepository.deleteAllByRoleId(role.getId());
    roleService.createRolePermissions(role.getId(), request.getPermissionIds(), millis);
    return new RoleUpdateResponse(roleMapper.rolePb(role, request.getPermissionIds()));
  }
}
