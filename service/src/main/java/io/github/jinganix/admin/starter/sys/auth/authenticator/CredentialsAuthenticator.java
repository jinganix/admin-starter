package io.github.jinganix.admin.starter.sys.auth.authenticator;

import io.github.jinganix.admin.starter.helper.auth.authenticator.Authenticator;
import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;
import io.github.jinganix.admin.starter.sys.auth.repository.UserCredentialRepository;
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

  private final UserCredentialRepository userCredentialRepository;

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

    UserCredential credential = userCredentialRepository.findByUsername(username);
    if (credential == null) {
      throw new UsernameNotFoundException(username);
    }
    if (!passwordEncoder.matches(password, credential.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }
    User user =
        userRepository
            .findById(credential.getId())
            .orElseThrow(() -> new UsernameNotFoundException(username));
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new DisabledException("User is inactive");
    }
    return new AuthUserToken(user.getId());
  }
}
