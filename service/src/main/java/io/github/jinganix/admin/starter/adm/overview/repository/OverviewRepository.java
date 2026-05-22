package io.github.jinganix.admin.starter.adm.overview.repository;

import static io.github.jinganix.admin.starter.schema.Tables.ADMIN_OVERVIEW;

import io.github.jinganix.admin.starter.adm.overview.OverviewMapper;
import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class OverviewRepository {

  private final DSLContext dsl;

  private final OverviewMapper recordMapper;

  public boolean existsByMonth(LocalDate month) {
    return dsl.fetchExists(ADMIN_OVERVIEW, ADMIN_OVERVIEW.MONTH.eq(month));
  }

  public List<Overview> findAllByMonthBefore(LocalDate month) {
    return dsl.selectFrom(ADMIN_OVERVIEW)
        .where(ADMIN_OVERVIEW.MONTH.lt(month))
        .orderBy(ADMIN_OVERVIEW.MONTH.desc())
        .fetch(recordMapper::toEntity);
  }

  @Transactional
  public void insert(Overview overview) {
    dsl.insertInto(ADMIN_OVERVIEW).set(recordMapper.toRecord(overview)).execute();
  }

  @Transactional
  public void incrementApiGet(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.API_GET, ADMIN_OVERVIEW.API_GET.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementApiPost(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.API_POST, ADMIN_OVERVIEW.API_POST.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementUserCreated(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.USER_CREATED, ADMIN_OVERVIEW.USER_CREATED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementUserDeleted(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.USER_DELETED, ADMIN_OVERVIEW.USER_DELETED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementRoleCreated(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.ROLE_CREATED, ADMIN_OVERVIEW.ROLE_CREATED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementRoleDeleted(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.ROLE_DELETED, ADMIN_OVERVIEW.ROLE_DELETED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementPermissionCreated(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.PERMISSION_CREATED, ADMIN_OVERVIEW.PERMISSION_CREATED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }

  @Transactional
  public void incrementPermissionDeleted(LocalDate month, int count) {
    dsl.update(ADMIN_OVERVIEW)
        .set(ADMIN_OVERVIEW.PERMISSION_DELETED, ADMIN_OVERVIEW.PERMISSION_DELETED.add(count))
        .where(ADMIN_OVERVIEW.MONTH.eq(month))
        .execute();
  }
}
