package io.github.jinganix.admin.starter.sys.permission;

import io.github.jinganix.admin.starter.proto.sys.permission.PermissionEditPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionPb;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionStatus;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import io.github.jinganix.admin.starter.sys.utils.MappingPaging;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

  PermissionStatus status(
      io.github.jinganix.admin.starter.proto.sys.permission.PermissionStatus status);

  List<PermissionType> types(
      List<io.github.jinganix.admin.starter.proto.sys.permission.PermissionType> types);

  PermissionPb mapToPb(Permission permission);

  void fill(@MappingTarget Permission permission, PermissionEditPb pb);

  @MappingPaging
  PermissionListResponse page(Page<Permission> paging);

  PermissionUpdateStatusResponse updateStatusUpdate(Permission permission);
}
