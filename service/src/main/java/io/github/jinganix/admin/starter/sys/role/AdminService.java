package io.github.jinganix.admin.starter.sys.role;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;
import io.github.jinganix.admin.starter.sys.auth.repository.UserCredentialRepository;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.UserService;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  public static String ADMIN_ROLE_CODE = RoleCode.ADMIN.name();

  public static String ADMIN_USERNAME = ADMIN_ROLE_CODE.toLowerCase();

  private Long adminUserId;

  private Long adminRoleId;

  @Value("${config.admin.password}")
  private final String adminPassword;

  @Value("${config.admin.reset-pwd-when-updated-at}")
  private final Long resetPwdWhenUpdatedAt;

  private final Emitter emitter;

  private final RoleRepository roleRepository;

  private final UidGenerator uidGenerator;

  private final UserCredentialRepository userCredentialRepository;

  private final UserRoleRepository userRoleRepository;

  private final UserService userService;

  public boolean isAdminUser(Long userId) {
    return Objects.equals(adminUserId, userId);
  }

  public boolean isAdminRole(Long roleId) {
    return Objects.equals(adminRoleId, roleId);
  }

  public boolean hasAdminRole(List<UserRole> roles) {
    return roles.stream().anyMatch(x -> isAdminRole(x.getRoleId()));
  }

  public void initAdminData(long millis) {
    initAdminRole(millis);
    initAdminUser(millis);
    resetAdminPassword(millis);
  }

  private void initAdminRole(long millis) {
    Role role = roleRepository.findByCode(ADMIN_ROLE_CODE);
    if (role != null) {
      this.adminRoleId = role.getId();
      return;
    }
    this.adminRoleId = uidGenerator.nextUid();
    role =
        (Role)
            new Role()
                .setId(adminRoleId)
                .setCode(ADMIN_ROLE_CODE)
                .setName(ADMIN_ROLE_CODE.toLowerCase())
                .setDescription("admin.role.description")
                .setStatus(RoleStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    roleRepository.save(role);
    emitter.roleCreated(role);
  }

  private void initAdminUser(long millis) {
    UserCredential credential = userCredentialRepository.findByUsername(ADMIN_USERNAME);
    if (credential != null) {
      this.adminUserId = credential.getId();
      return;
    }
    User user = userService.createUser(ADMIN_USERNAME, adminPassword, millis);
    this.adminUserId = user.getId();
    Role role = roleRepository.findByCode(ADMIN_ROLE_CODE);
    UserRole userRole =
        (UserRole)
            new UserRole()
                .setId(uidGenerator.nextUid())
                .setUserId(user.getId())
                .setRoleId(role.getId())
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    userRoleRepository.save(userRole);
  }

  private void resetAdminPassword(long millis) {
    UserCredential credential =
        userCredentialRepository
            .findById(this.adminUserId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    if (Objects.equals(credential.getUpdatedAt(), resetPwdWhenUpdatedAt)) {
      userService.changePassword(credential.getId(), adminPassword, millis);
    }
  }
}
