package io.github.jinganix.admin.starter.sys.auth.repository;

import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserIdentityRepository extends JpaRepository<AdminUserIdentity, Long> {

  boolean existsByUsername(String username);

  AdminUserIdentity findByUsername(String username);

  AdminUserIdentity findByUserId(Long userId);

  void deleteAllByUserIdIn(Collection<Long> userIds);
}
