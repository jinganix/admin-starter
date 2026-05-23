package io.github.jinganix.admin.starter.sys.user;

import io.github.jinganix.admin.starter.proto.sys.user.UserCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserDetailsPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateRequest;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserIdentityRecord;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserRecord;
import io.github.jinganix.admin.starter.schema.tables.records.AdminUserRoleRecord;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.utils.MappingPaging;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  public abstract UserStatus status(
      io.github.jinganix.admin.starter.proto.sys.user.UserStatus status);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract void fill(@MappingTarget User user, UserUpdateProfileRequest pb);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "nickname", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract void fill(@MappingTarget User user, UserCreateRequest pb);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract void fill(@MappingTarget User user, UserUpdateRequest pb);

  public abstract UserCurrentPb currentPb(User user, String username, Set<String> authorities);

  public abstract UserDetailsPb detailsPb(User user, String username, List<Long> roleIds);

  public UserDetailsPb detailsPb(UserWithUsername user, List<Long> roleIds) {
    return detailsPb(user.getUser(), user.getUsername(), roleIds);
  }

  public abstract UserPb userPb(User user, String username);

  public UserPb userPb(UserWithUsername user) {
    return userPb(user.getUser(), user.getUsername());
  }

  public List<UserPb> userPbs(List<UserWithUsername> users) {
    return users.stream().map(this::userPb).toList();
  }

  @MappingPaging
  public abstract UserListResponse page(Page<UserWithUsername> paging);

  public abstract User toEntity(AdminUserRecord record);

  public abstract AdminUserRecord toRecord(User entity);

  public abstract AdminUserIdentity toEntity(AdminUserIdentityRecord record);

  public abstract AdminUserIdentityRecord toRecord(AdminUserIdentity entity);

  public abstract UserRole toEntity(AdminUserRoleRecord record);

  public abstract AdminUserRoleRecord toRecord(UserRole entity);
}
