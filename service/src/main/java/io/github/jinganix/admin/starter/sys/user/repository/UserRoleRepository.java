package io.github.jinganix.admin.starter.sys.user.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_USER_ROLE;

import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRoleRepository {

  private final DSLContext dsl;

  private final UserMapper recordMapper;

  public List<UserRole> findAllByUserId(Long userId) {
    return dsl.selectFrom(ADMIN_USER_ROLE)
        .where(ADMIN_USER_ROLE.USER_ID.eq(userId))
        .fetch(recordMapper::toEntity);
  }

  public List<UserRole> findAllByUserIdIn(Collection<Long> userIds) {
    return dsl.selectFrom(ADMIN_USER_ROLE)
        .where(ADMIN_USER_ROLE.USER_ID.in(userIds))
        .fetch(recordMapper::toEntity);
  }

  @Transactional
  public void deleteAllByUserId(Long userId) {
    dsl.deleteFrom(ADMIN_USER_ROLE).where(ADMIN_USER_ROLE.USER_ID.eq(userId)).execute();
  }

  @Transactional
  public void saveAll(List<UserRole> roles) {
    dsl.batchInsert(roles.stream().map(recordMapper::toRecord).toList()).execute();
  }
}
