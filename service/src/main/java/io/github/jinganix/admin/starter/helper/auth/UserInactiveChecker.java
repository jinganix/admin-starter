package io.github.jinganix.admin.starter.helper.auth;

public interface UserInactiveChecker {

  boolean isInactive(Long userId);
}
