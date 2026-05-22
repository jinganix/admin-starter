package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleUpdateStatusHandler {

  private final AdminService adminService;

  private final RoleMapper roleMapper;

  private final RoleRepository roleRepository;

  private final UtilsService utilsService;

  @Transactional
  public RoleUpdateStatusResponse handle(RoleUpdateStatusRequest request) {
    if (adminService.isAdminRole(request.getId())) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    Role role =
        roleRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.ROLE_NOT_FOUND));
    long millis = utilsService.currentTimeMillis();
    role.setStatus(roleMapper.status(request.getStatus())).setUpdatedAt(millis);
    roleRepository.save(role);
    return roleMapper.updateStatusUpdate(role);
  }
}
