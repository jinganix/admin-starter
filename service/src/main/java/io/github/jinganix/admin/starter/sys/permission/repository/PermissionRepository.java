package io.github.jinganix.admin.starter.sys.permission.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_PERMISSION;

import io.github.jinganix.admin.starter.helper.jooq.ConditionBuilder;
import io.github.jinganix.admin.starter.helper.jooq.PageableQuery;
import io.github.jinganix.admin.starter.schema.tables.records.AdminPermissionRecord;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
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
public class PermissionRepository {

  private final DSLContext dsl;

  private final PermissionMapper recordMapper;

  public boolean existsByCode(String code) {
    return dsl.fetchExists(ADMIN_PERMISSION, ADMIN_PERMISSION.CODE.eq(code));
  }

  public List<Permission> findAllByType(PermissionType type) {
    return dsl.selectFrom(ADMIN_PERMISSION)
        .where(ADMIN_PERMISSION.TYPE.eq(type))
        .fetch(recordMapper::toEntity);
  }

  public List<Permission> findAllByIdInAndTypeAndStatus(
      Collection<Long> ids, PermissionType type, PermissionStatus status) {
    return dsl.selectFrom(ADMIN_PERMISSION)
        .where(ADMIN_PERMISSION.ID.in(ids))
        .and(ADMIN_PERMISSION.TYPE.eq(type))
        .and(ADMIN_PERMISSION.STATUS.eq(status))
        .fetch(recordMapper::toEntity);
  }

  public Page<Permission> filter(
      Pageable pageable, String code, PermissionStatus status, List<PermissionType> types) {
    return PageableQuery.<AdminPermissionRecord, Permission>of(dsl, pageable)
        .mapper(recordMapper::toEntity)
        .fetch(
            dsl.selectFrom(ADMIN_PERMISSION)
                .where(
                    ConditionBuilder.builder()
                        .and(code == null ? null : ADMIN_PERMISSION.CODE.like("%" + code + "%"))
                        .and(status == null ? null : ADMIN_PERMISSION.STATUS.eq(status))
                        .and(types == null ? null : ADMIN_PERMISSION.TYPE.in(types))
                        .build()));
  }

  public Permission findById(Long id) {
    return dsl.selectFrom(ADMIN_PERMISSION)
        .where(ADMIN_PERMISSION.ID.eq(id))
        .fetchOne(recordMapper::toEntity);
  }

  public List<Permission> findAll() {
    return dsl.selectFrom(ADMIN_PERMISSION).fetch(recordMapper::toEntity);
  }

  public List<Permission> findAllById(Collection<Long> ids) {
    return dsl.selectFrom(ADMIN_PERMISSION)
        .where(ADMIN_PERMISSION.ID.in(ids))
        .fetch(recordMapper::toEntity);
  }

  @Transactional
  public void insert(Permission permission) {
    dsl.insertInto(ADMIN_PERMISSION).set(recordMapper.toRecord(permission)).execute();
  }

  @Transactional
  public void update(Permission permission) {
    dsl.update(ADMIN_PERMISSION)
        .set(recordMapper.toRecord(permission))
        .where(ADMIN_PERMISSION.ID.eq(permission.getId()))
        .execute();
  }

  @Transactional
  public void saveAll(List<Permission> permissions) {
    dsl.batchInsert(permissions.stream().map(recordMapper::toRecord).toList()).execute();
  }

  @Transactional
  public void deleteAllById(Collection<Long> ids) {
    dsl.deleteFrom(ADMIN_PERMISSION).where(ADMIN_PERMISSION.ID.in(ids)).execute();
  }
}
