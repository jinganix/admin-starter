package io.github.jinganix.admin.starter.adm.overview.repository;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OverviewRepository extends JpaRepository<Overview, Long> {

  boolean existsByMonth(LocalDate month);

  List<Overview> findAllByMonthBefore(LocalDate month);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.apiGet = x.apiGet + :count WHERE x.month = :month")
  void incrementApiGet(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.apiPost = x.apiPost + :count WHERE x.month = :month")
  void incrementApiPost(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.userCreated = x.userCreated + :count WHERE x.month = :month")
  void incrementUserCreated(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.userDeleted = x.userDeleted + :count WHERE x.month = :month")
  void incrementUserDeleted(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.roleCreated = x.roleCreated + :count WHERE x.month = :month")
  void incrementRoleCreated(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query("UPDATE Overview x SET x.roleDeleted = x.roleDeleted + :count WHERE x.month = :month")
  void incrementRoleDeleted(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Overview x SET x.permissionCreated = x.permissionCreated + :count WHERE x.month = :month")
  void incrementPermissionCreated(@Param("month") LocalDate month, @Param("count") int count);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Overview x SET x.permissionDeleted = x.permissionDeleted + :count WHERE x.month = :month")
  void incrementPermissionDeleted(@Param("month") LocalDate month, @Param("count") int count);
}
