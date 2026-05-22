package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordResponse;
import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;
import io.github.jinganix.admin.starter.sys.auth.repository.UserCredentialRepository;
import io.github.jinganix.admin.starter.sys.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserChangePasswordHandler {

  private final PasswordEncoder passwordEncoder;

  private final UtilsService utilsService;

  private final UserCredentialRepository userCredentialRepository;

  private final UserService userService;

  @Transactional
  public UserChangePasswordResponse handle(Long userId, UserChangePasswordRequest request) {
    UserCredential credential =
        userCredentialRepository
            .findById(userId)
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    if (!passwordEncoder.matches(request.getCurrent(), credential.getPassword())) {
      throw ApiException.of(ErrorCode.PASSWORD_NOT_MATCH);
    }
    long millis = utilsService.currentTimeMillis();
    userService.changePassword(userId, request.getPassword(), millis);
    return new UserChangePasswordResponse();
  }
}
