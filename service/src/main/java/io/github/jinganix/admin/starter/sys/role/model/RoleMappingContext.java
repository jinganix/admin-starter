package io.github.jinganix.admin.starter.sys.role.model;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;

@Getter
@RequiredArgsConstructor
public class RoleMappingContext {

  private final MultiValueMap<Long, Long> permissionIds;

  public List<Long> getPermissionIds(Long roleId) {
    return permissionIds.getOrDefault(roleId, Collections.emptyList());
  }
}
