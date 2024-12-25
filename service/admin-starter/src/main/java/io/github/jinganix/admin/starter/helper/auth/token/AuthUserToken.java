package io.github.jinganix.admin.starter.helper.auth.token;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class AuthUserToken implements Authentication {

  @Getter private final Long userId;

  @Setter private Set<GrantedAuthority> authorities = new HashSet<>();

  private boolean authenticated = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Object getCredentials() {
    return userId;
  }

  @Override
  public Object getDetails() {
    return userId;
  }

  @Override
  public Object getPrincipal() {
    return userId;
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return null;
  }
}
