package io.github.jinganix.admin.starter.sys.audit.model;

public interface AuditWithUsername {

  Audit getAudit();

  String getUsername();
}
