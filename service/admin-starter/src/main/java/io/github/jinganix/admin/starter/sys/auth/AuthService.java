package io.github.jinganix.admin.starter.sys.auth;

import io.github.jinganix.admin.starter.helper.auth.token.TokenService;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenResponse;
import io.github.jinganix.admin.starter.sys.auth.model.UserToken;
import io.github.jinganix.admin.starter.sys.auth.repository.UserTokenRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service. */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final TokenService tokenService;

  private final UserTokenRepository userTokenRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  public AuthTokenResponse createAuthTokenResponse(Long userId) {
    String accessToken = tokenService.generate(userId);
    String refreshToken = utilsService.uuid(true);

    long millis = utilsService.currentTimeMillis();
    UserToken token =
        (UserToken)
            new UserToken()
                .setId(uidGenerator.nextUid())
                .setRefreshToken(refreshToken)
                .setUserId(userId)
                .setCreatedAt(millis)
                .setUpdatedAt(millis);
    userTokenRepository.save(token);
    Long expiresIn = millis + TimeUnit.MINUTES.toMillis(5);
    return new AuthTokenResponse(accessToken, expiresIn, refreshToken);
  }
}
