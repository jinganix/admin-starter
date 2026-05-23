package io.github.jinganix.admin.starter.sys.auth.handler;

import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.AuthService;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthLoginHandler {

  private final AuthenticationManager authenticationManager;

  private final AuthService authService;

  private final UtilsService utilsService;

  public AuthTokenResponse handle(AuthLoginRequest request) {
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);
    Long userId = (Long) authentication.getPrincipal();
    long millis = utilsService.currentTimeMillis();
    AdminUserToken token = authService.createToken(millis, userId);
    return authService.createAuthTokenResponse(userId, token.getRefreshToken());
  }
}
