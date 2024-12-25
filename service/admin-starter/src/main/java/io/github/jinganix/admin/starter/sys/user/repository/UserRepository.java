package io.github.jinganix.admin.starter.sys.user.repository;

import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(
      "SELECT x AS user, u.username AS username FROM User x JOIN UserCredential u ON x.id = u.id WHERE x.id = :userId")
  Optional<UserWithUsername> findByIdWithUsername(@Param("userId") Long userId);

  @Query(
      "SELECT x AS user, u.username AS username FROM User x JOIN UserCredential u ON x.id = u.id "
          + "WHERE (:username IS NULL OR u.username like %:username%) "
          + "AND (:status IS NULL OR x.status = :status)")
  Page<UserWithUsername> filter(
      Pageable pageable, @Param("username") String username, @Param("status") UserStatus status);
}
