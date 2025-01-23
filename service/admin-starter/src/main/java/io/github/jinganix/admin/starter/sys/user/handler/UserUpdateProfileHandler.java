package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileResponse;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserUpdateProfileHandler {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final UtilsService utilsService;

  @Transactional
  public UserUpdateProfileResponse handle(Long userId, UserUpdateProfileRequest request) {
    UserWithUsername obj =
        userRepository
            .findByIdWithUsername(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    User user = obj.getUser();
    long millis = utilsService.currentTimeMillis();
    userMapper.fill(user, request);
    user.setUpdatedAt(millis);
    userRepository.save(user);
    return new UserUpdateProfileResponse(userMapper.userPb(obj));
  }
}
