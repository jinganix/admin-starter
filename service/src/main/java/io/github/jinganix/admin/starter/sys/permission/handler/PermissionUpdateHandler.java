package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateResponse;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionUpdateHandler {

  private final PermissionMapper permissionMapper;

  private final PermissionRepository permissionRepository;

  private final UtilsService utilsService;

  @Transactional
  public PermissionUpdateResponse handle(PermissionUpdateRequest request) {
    Permission permission =
        permissionRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.PERMISSION_NOT_FOUND));
    permissionMapper.fill(permission, request);
    long millis = utilsService.currentTimeMillis();
    permissionRepository.save((Permission) permission.setUpdatedAt(millis));
    return new PermissionUpdateResponse(permissionMapper.mapToPb(permission));
  }
}
