package io.github.jinganix.admin.starter.sys.user.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "sys_user")
public class User extends AbstractEntity {

  @Id private Long id;

  private String nickname;

  private UserStatus status;
}
