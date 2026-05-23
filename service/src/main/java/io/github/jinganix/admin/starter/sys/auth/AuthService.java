package io.github.jinganix.admin.starter.sys.auth;

import io.github.jinganix.admin.starter.helper.auth.token.TokenService;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserTokenRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service. */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final TokenService tokenService;

  private final AdminUserTokenRepository userTokenRepository;

  private final UtilsService utilsService;

  public AdminUserToken createToken(long millis, Long userId) {
    AdminUserToken token =
        new AdminUserToken()
            .setRefreshToken(utilsService.uuid(true))
            .setUserId(userId)
            .setCreatedAt(millis);
    userTokenRepository.insert(token);
    return token;
  }

  public AuthTokenResponse createAuthTokenResponse(Long userId, String refreshToken) {
    String accessToken = tokenService.generate(userId);
    Long expiresIn = utilsService.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
    return new AuthTokenResponse(accessToken, expiresIn, refreshToken);
  }
}
