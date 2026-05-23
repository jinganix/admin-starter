package io.github.jinganix.admin.starter.sys.role;

import io.github.jinganix.admin.starter.proto.sys.role.RoleEditPb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RolePb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusResponse;
import io.github.jinganix.admin.starter.schema.tables.records.AdminRolePermissionRecord;
import io.github.jinganix.admin.starter.schema.tables.records.AdminRoleRecord;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleMappingContext;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.utils.MappingPaging;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

  public abstract RoleStatus status(
      io.github.jinganix.admin.starter.proto.sys.role.RoleStatus status);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract void fill(@MappingTarget Role role, RoleEditPb pb);

  public abstract RolePb rolePb(Role role, List<Long> permissionIds);

  public List<RolePb> rolePbs(List<Role> roles, @Context RoleMappingContext ctx) {
    return roles.stream().map(x -> rolePb(x, ctx.getPermissionIds(x.getId()))).toList();
  }

  @MappingPaging
  public abstract RoleListResponse page(Page<Role> paging, @Context RoleMappingContext context);

  public abstract RoleUpdateStatusResponse updateStatusUpdate(Role role);

  public abstract Role toEntity(AdminRoleRecord record);

  public abstract AdminRoleRecord toRecord(Role entity);

  public abstract RolePermission toEntity(AdminRolePermissionRecord record);

  public abstract AdminRolePermissionRecord toRecord(RolePermission entity);
}
