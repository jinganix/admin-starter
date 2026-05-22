package io.github.jinganix.admin.starter.sys.auth.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthSignupRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.AuthService;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.role.RoleCode;
import io.github.jinganix.admin.starter.sys.user.UserService;
import io.github.jinganix.admin.starter.sys.user.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSignupHandler {

  private final AuthService authService;

  private final AdminUserIdentityRepository adminUserIdentityRepository;

  private final UserService userService;

  private final UtilsService utilsService;

  public AuthTokenResponse handle(AuthSignupRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();
    if (adminUserIdentityRepository.existsByUsername(username)) {
      throw ApiException.of(ErrorCode.USERNAME_EXISTS);
    }
    long millis = utilsService.currentTimeMillis();
    User user =
        userService.createUser(username, username, password, List.of(RoleCode.ADMIN), millis);
    AdminUserToken token = authService.createToken(millis, user.getId());
    return authService.createAuthTokenResponse(user.getId(), token.getRefreshToken());
  }
}
