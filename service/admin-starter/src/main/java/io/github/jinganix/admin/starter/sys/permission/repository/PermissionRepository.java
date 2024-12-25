package io.github.jinganix.admin.starter.sys.permission.repository;

import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

  boolean existsByCode(String code);

  List<Permission> findAllByType(PermissionType type);

  List<Permission> findAllByIdInAndTypeAndStatus(
      Collection<Long> ids, PermissionType type, PermissionStatus status);

  @Query(
      "SELECT x FROM Permission x WHERE (:code IS NULL OR x.code like %:code%) "
          + "AND (:status IS NULL OR x.status = :status) "
          + "AND (:types IS NULL OR x.type IN :types)")
  Page<Permission> filter(
      Pageable pageable,
      @Param("code") String code,
      @Param("status") PermissionStatus status,
      @Param("types") List<PermissionType> types);
}
