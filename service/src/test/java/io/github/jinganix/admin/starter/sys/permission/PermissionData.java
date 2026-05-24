package io.github.jinganix.admin.starter.sys.permission;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionEditPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionType;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateRequest;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;

public class PermissionData {

  public static final String CODE = "perm-code";

  public static final String NAME = "perm-name";

  public static Permission permission(long id) {
    return (Permission)
        new Permission()
            .setId(id)
            .setCode(CODE)
            .setName(NAME)
            .setDescription("description")
            .setType(io.github.jinganix.admin.starter.sys.permission.model.PermissionType.API)
            .setStatus(
                io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus.ACTIVE)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }

  public static PermissionEditPb editPb() {
    return new PermissionEditPb()
        .setName(NAME)
        .setCode(CODE)
        .setType(PermissionType.API)
        .setStatus(PermissionStatus.ACTIVE)
        .setDescription("description");
  }

  public static PermissionPb permissionPb(long id) {
    PermissionPb pb = new PermissionPb();
    pb.setId(id);
    pb.setName(NAME);
    pb.setCode(CODE);
    pb.setType(PermissionType.API);
    pb.setStatus(PermissionStatus.ACTIVE);
    pb.setDescription("description");
    pb.setCreatedAt(MIN_TIMESTAMP);
    return pb;
  }

  public static PermissionCreateRequest createRequest() {
    return (PermissionCreateRequest)
        new PermissionCreateRequest()
            .setName(NAME)
            .setCode(CODE)
            .setType(PermissionType.API)
            .setStatus(PermissionStatus.ACTIVE)
            .setDescription("description");
  }

  public static PermissionUpdateRequest updateRequest(long id) {
    return (PermissionUpdateRequest)
        new PermissionUpdateRequest()
            .setId(id)
            .setName(NAME)
            .setCode(CODE)
            .setType(PermissionType.API)
            .setStatus(PermissionStatus.ACTIVE)
            .setDescription("description");
  }
}
