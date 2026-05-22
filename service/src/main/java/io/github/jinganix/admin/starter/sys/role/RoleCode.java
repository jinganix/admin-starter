package io.github.jinganix.admin.starter.sys.role;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum RoleCode {
  ADMIN,
  AUTHED_USER;

  public static String toAuthority(String code) {
    return "ROLE_" + code;
  }

  public String getCode() {
    return toAuthority(this.name());
  }

  public GrantedAuthority authority() {
    return new SimpleGrantedAuthority(getCode());
  }
}
