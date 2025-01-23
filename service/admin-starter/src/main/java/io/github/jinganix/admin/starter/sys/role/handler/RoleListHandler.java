package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.proto.sys.role.RoleListRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListResponse;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleMappingContext;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleListHandler {

  private final RoleMapper roleMapper;

  private final RolePermissionRepository rolePermissionRepository;

  private final RoleRepository roleRepository;

  public RoleListResponse handle(Pageable pageable, RoleListRequest request) {
    Page<Role> roles =
        roleRepository.filter(pageable, request.getName(), roleMapper.status(request.getStatus()));
    RoleMappingContext contex =
        new RoleMappingContext(
            rolePermissionRepository
                .findAllByRoleIdIn(roles.stream().map(Role::getId).toList())
                .stream()
                .collect(
                    StreamUtils.toMultiMap(
                        RolePermission::getRoleId, RolePermission::getPermissionId)));
    return roleMapper.page(roles, contex);
  }
}
