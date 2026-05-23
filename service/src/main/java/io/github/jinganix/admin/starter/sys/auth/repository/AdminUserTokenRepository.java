package io.github.jinganix.admin.starter.sys.auth.repository;

import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import io.github.jinganix.admin.starter.sys.utils.CacheConst;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminUserTokenRepository {

  @Cacheable(
      cacheNames = CacheConst.ADMIN_USER_TOKEN,
      key = "#refreshToken",
      unless = "#result == null")
  public AdminUserToken findByRefreshToken(String refreshToken) {
    return null;
  }

  @CachePut(cacheNames = CacheConst.ADMIN_USER_TOKEN, key = "#entity.refreshToken")
  public AdminUserToken insert(AdminUserToken entity) {
    return entity;
  }

  @CacheEvict(cacheNames = CacheConst.ADMIN_USER_TOKEN, key = "#token")
  public void deleteByToken(String token) {}
}
