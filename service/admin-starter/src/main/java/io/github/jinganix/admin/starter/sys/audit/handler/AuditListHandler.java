package io.github.jinganix.admin.starter.sys.audit.handler;

import io.github.jinganix.admin.starter.proto.sys.audit.AuditListRequest;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListResponse;
import io.github.jinganix.admin.starter.sys.audit.AuditMapper;
import io.github.jinganix.admin.starter.sys.audit.model.AuditWithUsername;
import io.github.jinganix.admin.starter.sys.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditListHandler {

  private final AuditMapper auditMapper;

  private final AuditRepository auditRepository;

  public AuditListResponse handle(Pageable pageable, AuditListRequest request) {
    Page<AuditWithUsername> page =
        auditRepository.filter(
            pageable,
            request.getUserId(),
            request.getUsername(),
            request.getMethod(),
            request.getPath());
    return auditMapper.page(page);
  }
}
