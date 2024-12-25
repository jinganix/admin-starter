package io.github.jinganix.admin.starter.sys.audit;

import io.github.jinganix.admin.starter.proto.sys.audit.AuditListRequest;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListResponse;
import io.github.jinganix.admin.starter.sys.audit.handler.AuditListHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditController {

  private final AuditListHandler auditListHandler;

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_AUDIT_LIST)")
  @WebpbRequestMapping
  public AuditListResponse list(Pageable pageable, @Valid AuditListRequest request) {
    return auditListHandler.handle(pageable, request);
  }
}
