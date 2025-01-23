package io.github.jinganix.admin.starter.sys.role;

import static io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus.ACTIVE;

import io.github.jinganix.admin.starter.helper.auth.AuthorityService;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.permission.repository.PermissionRepository;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.role.repository.RolePermissionRepository;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleAuthorityService implements AuthorityService {

  private final Map<String, GrantedAuthority> apiAuthorities = new HashMap<>();

  private final Set<GrantedAuthority> adminAuthorities = new HashSet<>();

  private final AdminService adminService;

  private final PermissionRepository permissionRepository;

  private final RolePermissionRepository rolePermissionRepository;

  private final UserRoleRepository userRoleRepository;

  private final RoleRepository roleRepository;

  @PostConstruct
  void initialize() {
    for (Authority authority : Authority.values()) {
      apiAuthorities.put(authority.getValue(), new SimpleGrantedAuthority(authority.name()));
    }
    for (RoleCode roleCode : RoleCode.values()) {
      apiAuthorities.put(roleCode.name(), roleCode.authority());
    }
    this.adminAuthorities.add(RoleCode.ADMIN.authority());
    this.adminAuthorities.add(RoleCode.AUTHED_USER.authority());
    this.adminAuthorities.addAll(apiAuthorities.values());
  }

  @Override
  public Set<GrantedAuthority> getApiAuthorities(Long userId) {
    List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);
    if (adminService.hasAdminRole(userRoles)) {
      return adminAuthorities;
    }
    Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
    List<Role> roles =
        roleRepository.findAllById(roleIds).stream()
            .filter(x -> x.getStatus() == RoleStatus.ACTIVE)
            .toList();
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.addAll(getApiRoleAuthorities(roles));
    authorities.addAll(getApiAuthorities(roles));
    return authorities;
  }

  private Set<GrantedAuthority> getApiRoleAuthorities(List<Role> roles) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(RoleCode.AUTHED_USER.authority());
    roles.stream()
        .map(x -> apiAuthorities.get(x.getCode()))
        .filter(Objects::nonNull)
        .forEach(authorities::add);
    return authorities;
  }

  private Set<GrantedAuthority> getApiAuthorities(List<Role> roles) {
    List<Permission> permissions = getPermissions(roles, PermissionType.API);
    return permissions.stream()
        .map(x -> apiAuthorities.get(x.getCode()))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private List<Permission> getPermissions(List<Role> roles, PermissionType type) {
    if (roles.isEmpty()) {
      return Collections.emptyList();
    }
    List<Long> roleIds = roles.stream().map(Role::getId).toList();
    List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleIdIn(roleIds);
    List<Long> permissionIds =
        rolePermissions.stream().map(RolePermission::getPermissionId).toList();
    return permissionRepository.findAllByIdInAndTypeAndStatus(permissionIds, type, ACTIVE);
  }

  @Override
  public Set<String> getUiAuthorities(Long userId) {
    List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);
    Set<String> authorities = new HashSet<>();
    authorities.add(RoleCode.AUTHED_USER.getCode());

    List<Permission> permissions;
    if (adminService.hasAdminRole(userRoles)) {
      authorities.add(RoleCode.ADMIN.getCode());
      permissions = permissionRepository.findAllByType(PermissionType.UI);
    } else {
      Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
      List<Role> roles =
          roleRepository.findAllByIdInAndStatus(roleIds, RoleStatus.ACTIVE).stream().toList();
      authorities.addAll(roles.stream().map(x -> "ROLE_" + x.getCode()).toList());
      permissions = getPermissions(roles, PermissionType.UI);
    }
    authorities.addAll(permissions.stream().map(Permission::getCode).toList());
    return authorities;
  }
}
