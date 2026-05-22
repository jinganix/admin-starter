package io.github.jinganix.admin.starter.sys.audit;

import io.github.jinganix.admin.starter.proto.sys.audit.AuditListResponse;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditPb;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.model.AuditWithUsername;
import io.github.jinganix.admin.starter.sys.utils.MappingPaging;
import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public abstract class AuditMapper {

  public abstract AuditPb auditPb(Audit audit, String username);

  public AuditPb auditPb(AuditWithUsername audit) {
    return auditPb(audit.getAudit(), audit.getUsername());
  }

  public List<AuditPb> auditPbs(List<AuditWithUsername> audits) {
    return audits.stream().map(this::auditPb).toList();
  }

  @MappingPaging
  public abstract AuditListResponse page(Page<AuditWithUsername> paging);
}
