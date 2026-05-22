package io.github.jinganix.admin.starter.sys.permission.handler;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsResponse;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionOptionsHandler {

  private final PermissionRepository permissionRepository;

  public PermissionOptionsResponse handle() {
    List<Permission> permissions = permissionRepository.findAll();
    return new PermissionOptionsResponse(
        permissions.stream()
            .filter(x -> x.getStatus() == PermissionStatus.ACTIVE)
            .map(
                x ->
                    (PermissionOptionPb)
                        new PermissionOptionPb()
                            .setCode(x.getCode())
                            .setLabel(x.getName())
                            .setValue(x.getId() + ""))
            .toList());
  }
}
