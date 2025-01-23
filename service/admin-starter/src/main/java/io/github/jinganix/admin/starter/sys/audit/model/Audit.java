package io.github.jinganix.admin.starter.sys.audit.model;

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
@Table(name = "sys_audit")
public class Audit extends AbstractEntity {

  @Id private Long id;

  private Long userId;

  private String method;

  private String path;

  private String params;
}
