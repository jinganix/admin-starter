package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.auth.AuthorityService;
import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentResponse;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCurrentHandler {

  private final AuthorityService authorityService;

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  public UserCurrentResponse handle(Long userId) {
    UserWithUsername user =
        userRepository
            .findByIdWithUsername(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    return new UserCurrentResponse(
        userMapper.currentPb(
            user.getUser(), user.getUsername(), authorityService.getUiAuthorities(userId)));
  }
}
