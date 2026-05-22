package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadResponse;
import io.github.jinganix.admin.starter.sys.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionReloadHandler {

  private final PermissionService permissionService;

  private final UtilsService utilsService;

  @Transactional
  public PermissionReloadResponse handle() {
    permissionService.reload(utilsService.currentTimeMillis());
    return new PermissionReloadResponse();
  }
}
