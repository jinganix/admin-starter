package io.github.jinganix.admin.starter.helper.auth.authenticator;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationManagerImpl implements AuthenticationManager {

  private final List<Authenticator> authenticators;

  public AuthenticationManagerImpl(List<Authenticator> authenticators) {
    this.authenticators = authenticators;
  }

  @Override
  public Authentication authenticate(Authentication authenticationToken) {
    for (Authenticator authenticator : authenticators) {
      if (authenticator.support(authenticationToken)) {
        return authenticator.authenticate(authenticationToken);
      }
    }
    throw new RuntimeException("Unhandled authentication: " + authenticationToken.getClass());
  }
}
