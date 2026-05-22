package io.github.jinganix.admin.starter.sys.auth.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "admin_user_identity")
public class AdminUserIdentity extends AbstractEntity {

  @Id private Long id;

  private Long userId;

  @Enumerated(EnumType.ORDINAL)
  private AuthProvider provider = AuthProvider.USERNAME;

  private String username;

  private String password;

  private boolean verified;
}
