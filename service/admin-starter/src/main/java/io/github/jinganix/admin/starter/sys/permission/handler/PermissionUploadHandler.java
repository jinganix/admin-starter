package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionEditPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionUploadHandler {

  private final Emitter emitter;

  private final PermissionMapper permissionMapper;

  private final PermissionRepository permissionRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  @Transactional
  public PermissionUploadResponse handle(PermissionUploadRequest request) {
    long millis = utilsService.currentTimeMillis();
    List<Permission> permissions = new ArrayList<>();
    for (PermissionEditPb pb : request.getPermissions()) {
      if (permissionRepository.existsByCode(pb.getCode())) {
        continue;
      }
      Permission permission =
          (Permission)
              new Permission()
                  .setId(uidGenerator.nextUid())
                  .setStatus(PermissionStatus.ACTIVE)
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
      permissionMapper.fill(permission, pb);
      permissions.add(permission);
    }
    permissionRepository.saveAll(permissions);
    emitter.permissionsCreated(permissions);
    return new PermissionUploadResponse();
  }
}
