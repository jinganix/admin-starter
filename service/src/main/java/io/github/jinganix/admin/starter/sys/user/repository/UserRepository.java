package io.github.jinganix.admin.starter.sys.user.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_USER;
import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_USER_IDENTITY;

import io.github.jinganix.admin.starter.helper.jooq.ConditionBuilder;
import io.github.jinganix.admin.starter.helper.jooq.PageableQuery;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserIdentityRecord;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserRecord;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Records;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final DSLContext dsl;

  private final UserMapper recordMapper;

  public UserWithUsername findByIdWithUsername(Long userId) {
    return dsl.select(ADMIN_USER, ADMIN_USER_IDENTITY)
        .from(ADMIN_USER)
        .join(ADMIN_USER_IDENTITY)
        .on(ADMIN_USER.ID.eq(ADMIN_USER_IDENTITY.USER_ID))
        .where(ADMIN_USER.ID.eq(userId))
        .fetchOne(
            Records.mapping(
                (userRecord, identityRecord) -> {
                  UserWithUsername entity = new UserWithUsername();
                  entity.setUser(recordMapper.toEntity(userRecord));
                  entity.setUsername(identityRecord.getUsername());
                  return entity;
                }));
  }

  public Page<UserWithUsername> filter(
      Pageable pageable, Long userId, String username, UserStatus status) {
    return PageableQuery.<Record2<AdminUserRecord, AdminUserIdentityRecord>, UserWithUsername>of(
            dsl, pageable)
        .mapper(
            Records.mapping(
                (userRecord, identityRecord) -> {
                  UserWithUsername entity = new UserWithUsername();
                  entity.setUser(recordMapper.toEntity(userRecord));
                  entity.setUsername(identityRecord.getUsername());
                  return entity;
                }))
        .fetch(
            dsl.select(ADMIN_USER, ADMIN_USER_IDENTITY)
                .from(ADMIN_USER)
                .join(ADMIN_USER_IDENTITY)
                .on(ADMIN_USER.ID.eq(ADMIN_USER_IDENTITY.USER_ID))
                .where(
                    ConditionBuilder.builder()
                        .and(userId == null ? null : ADMIN_USER.ID.eq(userId))
                        .and(
                            username == null
                                ? null
                                : ADMIN_USER_IDENTITY.USERNAME.like("%" + username + "%"))
                        .and(status == null ? null : ADMIN_USER.STATUS.eq(status))
                        .build()));
  }

  public User findById(Long userId) {
    return dsl.selectFrom(ADMIN_USER)
        .where(ADMIN_USER.ID.eq(userId))
        .fetchOne(recordMapper::toEntity);
  }

  public List<User> findAllById(List<Long> userIds) {
    return dsl.selectFrom(ADMIN_USER)
        .where(ADMIN_USER.ID.in(userIds))
        .fetch(recordMapper::toEntity);
  }

  @Transactional
  public void deleteAllById(List<Long> userIds) {
    dsl.deleteFrom(ADMIN_USER).where(ADMIN_USER.ID.in(userIds)).execute();
  }

  @Transactional
  public void insert(User user) {
    dsl.insertInto(ADMIN_USER).set(recordMapper.toRecord(user)).execute();
  }

  @Transactional
  public void update(User user) {
    dsl.update(ADMIN_USER)
        .set(recordMapper.toRecord(user))
        .where(ADMIN_USER.ID.eq(user.getId()))
        .execute();
  }
}
