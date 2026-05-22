package io.github.jinganix.admin.starter.sys.audit.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_AUDIT;
import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_USER_IDENTITY;

import io.github.jinganix.admin.starter.helper.jooq.ConditionBuilder;
import io.github.jinganix.admin.starter.helper.jooq.PageableQuery;
import io.github.jinganix.admin.starter.schema.tables.records.AdminAuditRecord;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserIdentityRecord;
import io.github.jinganix.admin.starter.sys.audit.AuditMapper;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.model.AuditWithUsername;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Records;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditRepository {

  private final DSLContext dsl;

  private final AuditMapper auditMapper;

  public Page<AuditWithUsername> filter(
      Pageable pageable, Long userId, String username, String method, String path) {
    return PageableQuery.<Record2<AdminAuditRecord, AdminUserIdentityRecord>, AuditWithUsername>of(
            dsl, pageable)
        .mapper(
            Records.mapping(
                (auditRecord, identityRecord) -> {
                  AuditWithUsername entity = new AuditWithUsername();
                  entity.setAudit(auditMapper.toEntity(auditRecord));
                  entity.setUsername(identityRecord == null ? null : identityRecord.getUsername());
                  return entity;
                }))
        .fetch(
            dsl.select(ADMIN_AUDIT, ADMIN_USER_IDENTITY)
                .from(ADMIN_AUDIT)
                .leftJoin(ADMIN_USER_IDENTITY)
                .on(ADMIN_AUDIT.USER_ID.eq(ADMIN_USER_IDENTITY.USER_ID))
                .where(
                    ConditionBuilder.builder()
                        .and(userId == null ? null : ADMIN_AUDIT.USER_ID.eq(userId))
                        .and(
                            username == null
                                ? null
                                : ADMIN_USER_IDENTITY.USERNAME.like("%" + username + "%"))
                        .and(method == null ? null : ADMIN_AUDIT.METHOD.eq(method))
                        .and(path == null ? null : ADMIN_AUDIT.PATH.like("%" + path + "%"))
                        .build()));
  }

  public void insert(Audit audit) {
    dsl.insertInto(ADMIN_AUDIT).set(auditMapper.toRecord(audit)).execute();
  }
}
