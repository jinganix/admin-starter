package io.github.jinganix.admin.starter.adm.role;

import io.github.jinganix.admin.starter.sys.permission.Authority;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class PermissionUtils {

  public static Set<GrantedAuthority> permissions(Authority... authorities) {
    return Arrays.stream(authorities)
        .map(x -> new SimpleGrantedAuthority(x.name()))
        .collect(Collectors.toSet());
  }

  public static Set<GrantedAuthority> roles(String... roles) {
    return Arrays.stream(roles)
        .map(x -> new SimpleGrantedAuthority("ROLE_" + x))
        .collect(Collectors.toSet());
  }
}
