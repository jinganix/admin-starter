package io.github.jinganix.admin.starter.sys.auth.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.AuthService;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenHandler {

  private final AuthService authService;

  private final AdminUserTokenRepository userTokenRepository;

  private final UserRepository userRepository;

  private final UtilsService utilsService;

  public AuthTokenResponse handle(AuthTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    if (StringUtils.isEmpty(refreshToken)) {
      return new AuthTokenResponse();
    }
    AdminUserToken token = userTokenRepository.findByRefreshToken(refreshToken);
    if (token == null) {
      throw ApiException.of(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_REFRESH_TOKEN);
    }
    User user = userRepository.findById(token.getUserId());
    if (user == null) {
      throw ApiException.of(ErrorCode.USER_NOT_FOUND);
    }
    long millis = utilsService.currentTimeMillis();
    AdminUserToken newToken = authService.createToken(millis, user.getId());
    userTokenRepository.deleteByToken(refreshToken);
    return authService.createAuthTokenResponse(user.getId(), newToken.getRefreshToken());
  }
}
