package io.github.jinganix.admin.starter.sys.auth.repository;

import io.github.jinganix.admin.starter.sys.auth.model.UserToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

  Optional<UserToken> findByRefreshToken(String token);
}
