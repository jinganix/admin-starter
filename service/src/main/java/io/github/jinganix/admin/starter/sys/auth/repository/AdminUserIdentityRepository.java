package io.github.jinganix.admin.starter.sys.auth.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_USER_IDENTITY;

import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AdminUserIdentityRepository {

  private final DSLContext dsl;

  private final UserMapper recordMapper;

  public boolean existsByUsername(String username) {
    return dsl.fetchExists(ADMIN_USER_IDENTITY, ADMIN_USER_IDENTITY.USERNAME.eq(username));
  }

  public AdminUserIdentity findByUsername(String username) {
    return dsl.selectFrom(ADMIN_USER_IDENTITY)
        .where(ADMIN_USER_IDENTITY.USERNAME.eq(username))
        .fetchOne(recordMapper::toEntity);
  }

  public AdminUserIdentity findByUserId(Long userId) {
    return dsl.selectFrom(ADMIN_USER_IDENTITY)
        .where(ADMIN_USER_IDENTITY.USER_ID.eq(userId))
        .fetchOne(recordMapper::toEntity);
  }

  @Transactional
  public void deleteAllByUserIdIn(Collection<Long> userIds) {
    dsl.deleteFrom(ADMIN_USER_IDENTITY).where(ADMIN_USER_IDENTITY.USER_ID.in(userIds)).execute();
  }

  @Transactional
  public void insert(AdminUserIdentity entity) {
    dsl.insertInto(ADMIN_USER_IDENTITY).set(recordMapper.toRecord(entity)).execute();
  }

  @Transactional
  public void update(AdminUserIdentity entity) {
    dsl.update(ADMIN_USER_IDENTITY)
        .set(recordMapper.toRecord(entity))
        .where(ADMIN_USER_IDENTITY.ID.eq(entity.getId()))
        .execute();
  }
}
