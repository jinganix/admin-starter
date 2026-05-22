package io.github.jinganix.admin.starter.sys.permission;

import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionService {

  private final Emitter emitter;

  private final PermissionRepository permissionRepository;

  private final UidGenerator uidGenerator;

  @Transactional
  public void reload(long millis) {
    List<Permission> permissions = new ArrayList<>();
    for (Authority authority : Authority.values()) {
      String code = authority.getValue();
      if (permissionRepository.existsByCode(code)) {
        continue;
      }
      PermissionType type = code.endsWith("/") ? PermissionType.GROUP : PermissionType.API;
      Permission permission =
          (Permission)
              new Permission()
                  .setId(uidGenerator.nextUid())
                  .setCode(code)
                  .setName("authority" + code.replace("/", "."))
                  .setDescription("")
                  .setType(type)
                  .setStatus(PermissionStatus.ACTIVE)
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
      permissions.add(permission);
    }
    permissionRepository.saveAll(permissions);
    emitter.permissionsCreated(permissions);
  }
}
