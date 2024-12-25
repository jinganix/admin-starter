package io.github.jinganix.admin.starter.sys.auth.repository;

import io.github.jinganix.admin.starter.sys.auth.model.UserCredential;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

  boolean existsByUsername(String username);

  UserCredential findByUsername(String username);

  List<UserCredential> findAllByIdIn(Collection<Long> ids);
}
