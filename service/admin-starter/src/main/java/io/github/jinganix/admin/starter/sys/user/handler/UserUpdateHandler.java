package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.UserService;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserUpdateHandler {

  private final AdminService adminService;

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final UserRoleRepository userRoleRepository;

  private final UserService userService;

  private final UtilsService utilsService;

  @Transactional
  public UserUpdateResponse handle(UserUpdateRequest request) {
    if (adminService.isAdminUser(request.getId())) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    UserWithUsername obj =
        userRepository
            .findByIdWithUsername(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    User user = obj.getUser();
    long millis = utilsService.currentTimeMillis();
    userRepository.save(
        (User)
            user.setNickname(request.getNickname())
                .setStatus(userMapper.status(request.getStatus()))
                .setUpdatedAt(millis));
    userRoleRepository.deleteAllByUserId(user.getId());
    userService.createUserRoles(user.getId(), request.getRoleIds(), millis);
    return new UserUpdateResponse(userMapper.detailsPb(obj, request.getRoleIds()));
  }
}
