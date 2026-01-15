package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateResponse;
import io.github.jinganix.admin.starter.sys.auth.repository.UserCredentialRepository;
import io.github.jinganix.admin.starter.sys.user.UserService;
import io.github.jinganix.admin.starter.sys.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreateHandler {

  private final UserCredentialRepository userCredentialRepository;

  private final UserService userService;

  private final UtilsService utilsService;

  @Transactional
  public UserCreateResponse handle(UserCreateRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();
    if (userCredentialRepository.existsByUsername(username)) {
      throw ApiException.of(ErrorCode.USERNAME_EXISTS);
    }
    long millis = utilsService.currentTimeMillis();
    User user = userService.createUser(username, password, millis);
    userService.createUserRoles(user.getId(), request.getRoleIds(), millis);
    return new UserCreateResponse();
  }
}
