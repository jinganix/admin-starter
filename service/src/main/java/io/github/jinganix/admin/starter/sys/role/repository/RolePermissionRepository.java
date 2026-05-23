package io.github.jinganix.admin.starter.sys.role.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_ROLE_PERMISSION;

import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepository {

  private final DSLContext dsl;

  private final RoleMapper recordMapper;

  public List<RolePermission> findByRoleId(Long roleId) {
    return dsl.selectFrom(ADMIN_ROLE_PERMISSION)
        .where(ADMIN_ROLE_PERMISSION.ROLE_ID.eq(roleId))
        .fetch(recordMapper::toEntity);
  }

  public List<RolePermission> findAllByRoleIdIn(Collection<Long> roleIds) {
    return dsl.selectFrom(ADMIN_ROLE_PERMISSION)
        .where(ADMIN_ROLE_PERMISSION.ROLE_ID.in(roleIds))
        .fetch(recordMapper::toEntity);
  }

  @Transactional
  public void deleteAllByRoleId(Long roleId) {
    dsl.deleteFrom(ADMIN_ROLE_PERMISSION).where(ADMIN_ROLE_PERMISSION.ROLE_ID.eq(roleId)).execute();
  }

  @Transactional
  public void saveAll(List<RolePermission> rolePermissions) {
    dsl.batchInsert(rolePermissions.stream().map(recordMapper::toRecord).toList()).execute();
  }
}
