package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveResponse;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleRetrieveHandler {

  private final RoleMapper roleMapper;

  private final RolePermissionRepository rolePermissionRepository;

  private final RoleRepository roleRepository;

  @Transactional
  public RoleRetrieveResponse handle(RoleRetrieveRequest request) {
    Role role =
        roleRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.ROLE_NOT_FOUND));
    List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
    return new RoleRetrieveResponse(
        roleMapper.rolePb(
            role, rolePermissions.stream().map(RolePermission::getPermissionId).toList()));
  }
}
