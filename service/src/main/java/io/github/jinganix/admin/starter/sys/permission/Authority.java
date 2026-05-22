package io.github.jinganix.admin.starter.sys.permission;

import io.github.jinganix.admin.starter.helper.enumeration.StringEnumMapper;
import io.github.jinganix.webpb.runtime.enumeration.Enumeration;

public enum Authority implements Enumeration<String> {
  ADM("/adm/"),
  ADM_OVERVIEW("/adm/overview/"),
  ADM_OVERVIEW_LIST("/adm/overview/list"),
  SYS("/sys/"),
  SYS_AUDIT("/sys/audit/"),
  SYS_AUDIT_LIST("/sys/audit/list"),
  SYS_PERMISSION("/sys/permission/"),
  SYS_PERMISSION_CREATE("/sys/permission/create"),
  SYS_PERMISSION_DELETE("/sys/permission/delete"),
  SYS_PERMISSION_LIST("/sys/permission/list"),
  SYS_PERMISSION_OPTIONS("/sys/permission/options"),
  SYS_PERMISSION_RELOAD("/sys/permission/reload"),
  SYS_PERMISSION_STATUS("/sys/permission/status"),
  SYS_PERMISSION_UPDATE("/sys/permission/update"),
  SYS_PERMISSION_UPLOAD("/sys/permission/upload"),
  SYS_ROLE("/sys/role/"),
  SYS_ROLE_CREATE("/sys/role/create"),
  SYS_ROLE_DELETE("/sys/role/delete"),
  SYS_ROLE_GET("/sys/role/get"),
  SYS_ROLE_LIST("/sys/role/list"),
  SYS_ROLE_OPTIONS("/sys/role/options"),
  SYS_ROLE_STATUS("/sys/role/status"),
  SYS_ROLE_UPDATE("/sys/role/update"),
  SYS_USER("/sys/user/"),
  SYS_USER_CREATE("/sys/user/create"),
  SYS_USER_DELETE("/sys/user/delete"),
  SYS_USER_LIST("/sys/user/list"),
  SYS_USER_STATUS("/sys/user/status"),
  SYS_USER_UPDATE("/sys/user/update");

  private static final StringEnumMapper<Authority> mapper = new StringEnumMapper<>(values());

  private final String value;

  Authority(String value) {
    this.value = value;
  }

  public static Authority fromValue(String value) {
    return mapper.fromValue(value);
  }

  @Override
  public String getValue() {
    return this.value;
  }
}
