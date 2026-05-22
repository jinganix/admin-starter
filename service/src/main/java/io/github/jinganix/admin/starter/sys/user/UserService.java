package io.github.jinganix.admin.starter.sys.user;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;
import io.github.jinganix.admin.starter.sys.auth.repository.UserCredentialRepository;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class UserService {

  private final Emitter emitter;

  private final PasswordEncoder passwordEncoder;

  private final RoleRepository roleRepository;

  private final UserRoleRepository userRoleRepository;

  private final UserCredentialRepository userCredentialRepository;

  private final UserRepository userRepository;

  private final UidGenerator uidGenerator;

  @Transactional
  public User createUser(String username, String password, long millis) {
    return createUser(username, username, password, Collections.emptyList(), millis);
  }

  @Transactional
  public User createUser(
      String nickname, String username, String password, List<RoleCode> codes, long millis) {
    long userId = uidGenerator.nextUid();
    UserCredential credential =
        (UserCredential)
            new UserCredential()
                .setId(userId)
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password))
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    User user =
        (User)
            new User()
                .setId(userId)
                .setNickname(nickname)
                .setStatus(UserStatus.ACTIVE)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    userCredentialRepository.save(credential);
    userRepository.save(user);
    if (!CollectionUtils.isEmpty(codes)) {
      List<UserRole> userRoles =
          roleRepository.findAllByCodeIn(codes.stream().map(Enum::name).toList()).stream()
              .map(
                  x ->
                      (UserRole)
                          new UserRole()
                              .setId(uidGenerator.nextUid())
                              .setUserId(userId)
                              .setRoleId(x.getId())
                              .setCreatedAt(millis)
                              .setUpdatedAt(millis))
              .toList();
      userRoleRepository.saveAll(userRoles);
    }
    emitter.userCreated(user);
    return user;
  }

  @Transactional
  public void changePassword(Long userId, String password, long millis) {
    UserCredential credential =
        userCredentialRepository
            .findById(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    credential.setPassword(passwordEncoder.encode(password)).setUpdatedAt(millis);
    userCredentialRepository.save(credential);
  }

  public void createUserRoles(Long userId, List<Long> roleIds, long millis) {
    List<Role> roles = roleRepository.findAllById(roleIds);
    List<UserRole> userRoles =
        roles.stream()
            .map(
                x ->
                    (UserRole)
                        new UserRole()
                            .setId(uidGenerator.nextUid())
                            .setUserId(userId)
                            .setRoleId(x.getId())
                            .setCreatedAt(millis)
                            .setUpdatedAt(millis))
            .toList();
    userRoleRepository.saveAll(userRoles);
  }
}
