package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListResponse;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionListHandler {

  private final PermissionMapper permissionMapper;

  private final PermissionRepository permissionRepository;

  public PermissionListResponse handle(Pageable pageable, PermissionListRequest request) {
    return permissionMapper.page(
        permissionRepository.filter(
            pageable,
            request.getCode(),
            permissionMapper.status(request.getStatus()),
            permissionMapper.types(request.getTypes())));
  }
}
