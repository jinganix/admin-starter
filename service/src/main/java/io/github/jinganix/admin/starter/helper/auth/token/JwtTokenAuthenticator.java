package io.github.jinganix.admin.starter.helper.auth.token;

import io.github.jinganix.admin.starter.helper.auth.AuthorityService;
import io.github.jinganix.admin.starter.helper.auth.UserInactiveChecker;
import io.github.jinganix.admin.starter.helper.auth.authenticator.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticator implements Authenticator {

  private final TokenService tokenService;

  private final UserInactiveChecker userInactiveChecker;

  private AuthorityService authorityService;

  @Autowired(required = false)
  public void setAuthorityService(AuthorityService authorityService) {
    this.authorityService = authorityService;
  }

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof BearerTokenAuthenticationToken;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    String token = ((BearerTokenAuthenticationToken) authentication).getToken();
    AuthUserToken userToken = tokenService.decode(token);
    if (userToken == null) {
      throw new InvalidBearerTokenException("Token is invalid");
    }
    if (userInactiveChecker.isInactive(userToken.getUserId())) {
      throw new DisabledException("User is inactive");
    }
    if (authorityService != null) {
      userToken.setAuthorities(authorityService.getApiAuthorities(userToken.getUserId()));
    }
    return userToken;
  }
}
