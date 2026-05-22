package io.github.jinganix.admin.starter.sys.auth.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUserIdentity extends AbstractEntity {

  private Long id;

  private Long userId;

  private AuthProvider provider = AuthProvider.USERNAME;

  private String username;

  private String password;

  private boolean verified;
}
