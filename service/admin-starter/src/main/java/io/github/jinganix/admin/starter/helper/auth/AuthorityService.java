package io.github.jinganix.admin.starter.helper.auth;

import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public interface AuthorityService {

  Set<GrantedAuthority> getApiAuthorities(Long userId);

  Set<String> getUiAuthorities(Long userId);
}
