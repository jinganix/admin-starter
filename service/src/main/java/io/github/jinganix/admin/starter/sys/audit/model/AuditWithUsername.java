package io.github.jinganix.admin.starter.sys.audit.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditWithUsername {

  private Audit audit;

  private String username;
}
