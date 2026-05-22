package io.github.jinganix.admin.starter.sys.auth.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.AuthService;
import io.github.jinganix.admin.starter.sys.auth.model.UserToken;
import io.github.jinganix.admin.starter.sys.auth.repository.UserTokenRepository;
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

  private final UserTokenRepository userTokenRepository;

  private final UserRepository userRepository;

  public AuthTokenResponse handle(AuthTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    if (StringUtils.isEmpty(refreshToken)) {
      return new AuthTokenResponse();
    }
    UserToken token =
        userTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(
                () -> ApiException.of(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_REFRESH_TOKEN));
    userTokenRepository.deleteById(token.getId());
    User user =
        userRepository
            .findById(token.getUserId())
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    return authService.createAuthTokenResponse(user.getId());
  }
}
