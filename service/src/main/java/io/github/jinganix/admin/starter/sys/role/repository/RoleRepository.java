package io.github.jinganix.admin.starter.sys.role.repository;

import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  boolean existsByCode(String code);

  Role findByCode(String code);

  List<Role> findAllByCodeIn(Collection<String> codes);

  List<Role> findAllByIdInAndStatus(Collection<Long> ids, RoleStatus status);

  @Query(
      "SELECT x FROM Role x WHERE (:name IS NULL OR x.name like %:name%) "
          + "AND (:status IS NULL OR x.status = :status)")
  Page<Role> filter(
      Pageable pageable, @Param("name") String name, @Param("status") RoleStatus status);
}
