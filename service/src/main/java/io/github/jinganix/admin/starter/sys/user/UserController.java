package io.github.jinganix.admin.starter.sys.user;

import io.github.jinganix.admin.starter.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserChangePasswordResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserCurrentResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserListRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateProfileResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserUpdateStatusResponse;
import io.github.jinganix.admin.starter.setup.argument.annotations.UserId;
import io.github.jinganix.admin.starter.sys.user.handler.UserChangePasswordHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserCreateHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserCurrentHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserDeleteHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserListHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserRetrieveHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserUpdateHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserUpdateProfileHandler;
import io.github.jinganix.admin.starter.sys.user.handler.UserUpdateStatusHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final UserChangePasswordHandler userChangePasswordHandler;

  private final UserCreateHandler userCreateHandler;

  private final UserCurrentHandler userCurrentHandler;

  private final UserDeleteHandler userDeleteHandler;

  private final UserListHandler userListHandler;

  private final UserRetrieveHandler userRetrieveHandler;

  private final UserUpdateHandler userUpdateHandler;

  private final UserUpdateProfileHandler userUpdateProfileHandler;

  private final UserUpdateStatusHandler userUpdateStatusHandler;

  @PreAuthorize("hasRole(T(io.github.jinganix.admin.starter.sys.role.RoleCode).AUTHED_USER)")
  @WebpbRequestMapping
  public UserChangePasswordResponse changePassword(
      @UserId Long userId, @Valid @RequestBody UserChangePasswordRequest request) {
    return userChangePasswordHandler.handle(userId, request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_CREATE)")
  @WebpbRequestMapping
  public UserCreateResponse create(@Valid @RequestBody UserCreateRequest request) {
    return orderedTraceExecutor.supply("USER_CREATE", () -> userCreateHandler.handle(request));
  }

  @PreAuthorize("hasRole(T(io.github.jinganix.admin.starter.sys.role.RoleCode).AUTHED_USER)")
  @WebpbRequestMapping(message = UserCurrentRequest.class)
  public UserCurrentResponse current(@UserId Long userId) {
    return userCurrentHandler.handle(userId);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_DELETE)")
  @WebpbRequestMapping
  public UserDeleteResponse delete(@Valid @RequestBody UserDeleteRequest request) {
    return orderedTraceExecutor.supply("USER_DELETE", () -> userDeleteHandler.handle(request));
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_LIST)")
  @WebpbRequestMapping
  public UserListResponse list(Pageable pageable, @Valid @ModelAttribute UserListRequest request) {
    return userListHandler.handle(pageable, request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_LIST)")
  @WebpbRequestMapping
  public UserRetrieveResponse retrieve(@Valid @ModelAttribute UserRetrieveRequest request) {
    return userRetrieveHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_UPDATE)")
  @WebpbRequestMapping
  public UserUpdateResponse update(@Valid @RequestBody UserUpdateRequest request) {
    return userUpdateHandler.handle(request);
  }

  @PreAuthorize("hasRole(T(io.github.jinganix.admin.starter.sys.role.RoleCode).AUTHED_USER)")
  @WebpbRequestMapping
  public UserUpdateProfileResponse updateProfile(
      @UserId Long userId, @Valid @RequestBody UserUpdateProfileRequest request) {
    return userUpdateProfileHandler.handle(userId, request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_USER_STATUS)")
  @WebpbRequestMapping
  public UserUpdateStatusResponse updateStatus(
      @Valid @RequestBody UserUpdateStatusRequest request) {
    return userUpdateStatusHandler.handle(request);
  }
}
