package io.github.jinganix.admin.starter.sys.role.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_ROLE;

import io.github.jinganix.admin.starter.helper.jooq.ConditionBuilder;
import io.github.jinganix.admin.starter.helper.jooq.PageableQuery;
import io.github.jinganix.admin.starter.schema.tables.records.AdminRoleRecord;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

  private final DSLContext dsl;

  private final RoleMapper recordMapper;

  public boolean existsByCode(String code) {
    return dsl.fetchExists(dsl.selectFrom(ADMIN_ROLE).where(ADMIN_ROLE.CODE.eq(code)));
  }

  public Role findByCode(String code) {
    return dsl.selectFrom(ADMIN_ROLE)
        .where(ADMIN_ROLE.CODE.eq(code))
        .fetchOne(recordMapper::toEntity);
  }

  public List<Role> findAllByCodeIn(Collection<String> codes) {
    return dsl.selectFrom(ADMIN_ROLE)
        .where(ADMIN_ROLE.CODE.in(codes))
        .fetch(recordMapper::toEntity);
  }

  public List<Role> findAllByIdInAndStatus(Collection<Long> ids, RoleStatus status) {
    return dsl.selectFrom(ADMIN_ROLE)
        .where(ADMIN_ROLE.ID.in(ids).and(ADMIN_ROLE.STATUS.eq(status)))
        .fetch(recordMapper::toEntity);
  }

  public Page<Role> filter(Pageable pageable, String name, RoleStatus status) {
    return PageableQuery.<AdminRoleRecord, Role>of(dsl, pageable)
        .mapper(recordMapper::toEntity)
        .fetch(
            dsl.selectFrom(ADMIN_ROLE)
                .where(
                    ConditionBuilder.builder()
                        .and(name == null ? null : ADMIN_ROLE.NAME.like("%" + name + "%"))
                        .and(status == null ? null : ADMIN_ROLE.STATUS.eq(status))
                        .build()));
  }

  public List<Role> findAllById(Collection<Long> ids) {
    return dsl.selectFrom(ADMIN_ROLE).where(ADMIN_ROLE.ID.in(ids)).fetch(recordMapper::toEntity);
  }

  public Role findById(Long id) {
    return dsl.selectFrom(ADMIN_ROLE).where(ADMIN_ROLE.ID.eq(id)).fetchOne(recordMapper::toEntity);
  }

  public List<Role> findAll() {
    return dsl.selectFrom(ADMIN_ROLE).fetch(recordMapper::toEntity);
  }

  @Transactional
  public void insert(Role role) {
    dsl.insertInto(ADMIN_ROLE).set(recordMapper.toRecord(role)).execute();
  }

  @Transactional
  public void update(Role role) {
    dsl.update(ADMIN_ROLE)
        .set(recordMapper.toRecord(role))
        .where(ADMIN_ROLE.ID.eq(role.getId()))
        .execute();
  }

  @Transactional
  public void deleteAllById(Collection<Long> ids) {
    dsl.deleteFrom(ADMIN_ROLE).where(ADMIN_ROLE.ID.in(ids)).execute();
  }
}
