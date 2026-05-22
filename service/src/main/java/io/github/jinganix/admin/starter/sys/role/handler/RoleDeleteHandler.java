package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.utils.BizUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleDeleteHandler {

  private final AdminService adminService;

  private final Emitter emitter;

  private final RoleRepository roleRepository;

  @Transactional
  public RoleDeleteResponse handle(RoleDeleteRequest request) {
    if (request.getIds().stream().anyMatch(adminService::isAdminRole)) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    List<Role> roles = roleRepository.findAllById(request.getIds());
    List<Long> roleIds = roles.stream().map(Role::getId).toList();
    if (BizUtils.notEquals(roleIds, request.getIds())) {
      throw ApiException.of(ErrorCode.ROLE_NOT_FOUND);
    }
    roleRepository.deleteAllById(request.getIds());
    emitter.roleDeleted(request.getIds());
    return new RoleDeleteResponse();
  }
}
