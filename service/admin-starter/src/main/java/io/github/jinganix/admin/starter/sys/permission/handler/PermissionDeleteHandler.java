package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.sys.utils.BizUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionDeleteHandler {

  private final Emitter emitter;

  private final PermissionRepository permissionRepository;

  @Transactional
  public PermissionDeleteResponse handle(PermissionDeleteRequest request) {
    List<Permission> permissions = permissionRepository.findAllById(request.getIds());
    if (BizUtils.notEquals(
        permissions.stream().map(Permission::getId).toList(), request.getIds())) {
      throw ApiException.of(ErrorCode.PERMISSION_NOT_FOUND);
    }
    permissionRepository.deleteAllById(request.getIds());
    emitter.permissionDeleted(request.getIds());
    return new PermissionDeleteResponse();
  }
}
