package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.RoleService;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleCreateHandler {

  private final Emitter emitter;

  private final RoleMapper roleMapper;

  private final RoleRepository roleRepository;

  private final RoleService roleService;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  @Transactional
  public RoleCreateResponse handle(RoleCreateRequest request) {
    if (roleRepository.existsByCode(request.getCode())) {
      throw ApiException.of(ErrorCode.ROLE_EXISTS);
    }
    long millis = utilsService.currentTimeMillis();
    Role role =
        (Role)
            new Role()
                .setId(uidGenerator.nextUid())
                .setStatus(RoleStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    roleMapper.fill(role, request);
    roleRepository.save(role);
    roleService.createRolePermissions(role.getId(), request.getPermissionIds(), millis);
    emitter.roleCreated(role);
    return new RoleCreateResponse();
  }
}
