package io.github.jinganix.admin.starter.sys.role.repository;

import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

  @Query
  List<RolePermission> findByRoleId(Long roleId);

  @Query
  List<RolePermission> findAllByRoleIdIn(Collection<Long> roleIds);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM RolePermission WHERE roleId=(:roleId)")
  void deleteAllByRoleId(Long roleId);
}
