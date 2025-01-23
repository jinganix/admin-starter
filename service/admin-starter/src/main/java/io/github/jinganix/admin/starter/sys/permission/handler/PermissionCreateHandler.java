package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionCreateHandler {

  private final Emitter emitter;

  private final PermissionMapper permissionMapper;

  private final PermissionRepository permissionRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  @Transactional
  public PermissionCreateResponse handle(PermissionCreateRequest request) {
    if (permissionRepository.existsByCode(request.getCode())) {
      throw ApiException.of(ErrorCode.PERMISSION_EXISTS);
    }
    long millis = utilsService.currentTimeMillis();
    Permission permission =
        (Permission)
            new Permission()
                .setId(uidGenerator.nextUid())
                .setStatus(PermissionStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    permissionMapper.fill(permission, request);
    permissionRepository.save(permission);
    emitter.permissionsCreated(List.of(permission));
    return new PermissionCreateResponse(permissionMapper.mapToPb(permission));
  }
}
