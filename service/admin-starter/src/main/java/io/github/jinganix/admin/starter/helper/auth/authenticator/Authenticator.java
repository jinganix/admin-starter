package io.github.jinganix.admin.starter.helper.auth.authenticator;

import org.springframework.security.core.Authentication;

public interface Authenticator {

  boolean support(Authentication authentication);

  Authentication authenticate(Authentication authentication);
}
