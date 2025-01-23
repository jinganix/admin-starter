package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveResponse;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionRetrieveHandler {

  private final PermissionMapper permissionMapper;

  private final PermissionRepository permissionRepository;

  @Transactional
  public PermissionRetrieveResponse handle(PermissionRetrieveRequest request) {
    Permission permission =
        permissionRepository
            .findById(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.PERMISSION_NOT_FOUND));
    return new PermissionRetrieveResponse(permissionMapper.mapToPb(permission));
  }
}
