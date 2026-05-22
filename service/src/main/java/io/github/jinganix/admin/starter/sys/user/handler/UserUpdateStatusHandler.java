package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserUpdateStatusHandler {

  private final AdminService adminService;

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final UtilsService utilsService;

  @Transactional
  public UserUpdateStatusResponse handle(UserUpdateStatusRequest request) {
    if (adminService.isAdminUser(request.getId())) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    UserWithUsername obj =
        userRepository
            .findByIdWithUsername(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    User user = obj.getUser();
    long millis = utilsService.currentTimeMillis();
    user.setStatus(userMapper.status(request.getStatus())).setUpdatedAt(millis);
    userRepository.save(user);
    return new UserUpdateStatusResponse(userMapper.userPb(obj));
  }
}
