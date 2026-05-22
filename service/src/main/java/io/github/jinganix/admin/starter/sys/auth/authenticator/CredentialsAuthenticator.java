package io.github.jinganix.admin.starter.sys.auth.authenticator;

import io.github.jinganix.admin.starter.helper.auth.authenticator.Authenticator;
import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsAuthenticator implements Authenticator {

  private final PasswordEncoder passwordEncoder;

  private final AdminUserIdentityRepository adminUserIdentityRepository;

  private final UserRepository userRepository;

  @Override
  public boolean support(Authentication authentication) {
    return authentication instanceof UsernamePasswordAuthenticationToken;
  }

  @Override
  public AuthUserToken authenticate(Authentication auth) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
    String username = (String) token.getPrincipal();
    String password = (String) token.getCredentials();

    AdminUserIdentity identity = adminUserIdentityRepository.findByUsername(username);
    if (identity == null) {
      throw new UsernameNotFoundException(username);
    }
    if (!passwordEncoder.matches(password, identity.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }
    User user =
        userRepository
            .findById(identity.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException(username));
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new DisabledException("User is inactive");
    }
    return new AuthUserToken(user.getId());
  }
}
