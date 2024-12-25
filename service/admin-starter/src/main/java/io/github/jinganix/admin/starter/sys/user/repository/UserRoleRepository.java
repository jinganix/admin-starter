package io.github.jinganix.admin.starter.sys.user.repository;

import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

  List<UserRole> findAllByUserId(Long userId);

  List<UserRole> findAllByUserIdIn(Collection<Long> userIds);

  void deleteAllByUserId(Long userId);
}
