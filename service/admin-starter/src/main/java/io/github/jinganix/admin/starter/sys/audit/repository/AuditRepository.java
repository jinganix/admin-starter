package io.github.jinganix.admin.starter.sys.audit.repository;

import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.model.AuditWithUsername;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

  @Query(
      "SELECT x AS audit, u.username AS username FROM Audit x JOIN UserCredential u ON x.userId = u.id "
          + "WHERE (:username IS NULL OR u.username LIKE %:username%) "
          + "AND (:method IS NULL OR x.method = :method) "
          + "AND (:path IS NULL OR x.path LIKE %:path%)")
  Page<AuditWithUsername> filter(
      Pageable pageable,
      @Param("username") String username,
      @Param("method") String method,
      @Param("path") String path);
}
