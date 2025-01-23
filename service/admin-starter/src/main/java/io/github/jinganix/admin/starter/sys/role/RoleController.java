package io.github.jinganix.admin.starter.sys.role;

import io.github.jinganix.admin.starter.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleDeleteResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleListResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.role.RoleUpdateStatusResponse;
import io.github.jinganix.admin.starter.sys.role.handler.RoleCreateHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleDeleteHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleListHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleOptionsHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleRetrieveHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleUpdateHandler;
import io.github.jinganix.admin.starter.sys.role.handler.RoleUpdateStatusHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoleController {

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final RoleCreateHandler roleCreateHandler;

  private final RoleDeleteHandler roleDeleteHandler;

  private final RoleListHandler roleListHandler;

  private final RoleOptionsHandler roleOptionsHandler;

  private final RoleRetrieveHandler roleRetrieveHandler;

  private final RoleUpdateHandler roleUpdateHandler;

  private final RoleUpdateStatusHandler roleUpdateStatusHandler;

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_CREATE)")
  @WebpbRequestMapping
  public RoleCreateResponse create(@Valid @RequestBody RoleCreateRequest request) {
    return orderedTraceExecutor.supply("ROLE_CREATE", () -> roleCreateHandler.handle(request));
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_DELETE)")
  @WebpbRequestMapping
  public RoleDeleteResponse delete(@Valid @RequestBody RoleDeleteRequest request) {
    return orderedTraceExecutor.supply("ROLE_DELETE", () -> roleDeleteHandler.handle(request));
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_LIST)")
  @WebpbRequestMapping
  public RoleListResponse list(Pageable pageable, @Valid RoleListRequest request) {
    return roleListHandler.handle(pageable, request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_OPTIONS)")
  @WebpbRequestMapping(message = RoleOptionsRequest.class)
  public RoleOptionsResponse options() {
    return roleOptionsHandler.handle();
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_GET)")
  @WebpbRequestMapping
  public RoleRetrieveResponse retrieve(@Valid RoleRetrieveRequest request) {
    return roleRetrieveHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_UPDATE)")
  @WebpbRequestMapping
  public RoleUpdateResponse update(@Valid @RequestBody RoleUpdateRequest request) {
    return roleUpdateHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_ROLE_STATUS)")
  @WebpbRequestMapping
  public RoleUpdateStatusResponse updateStatus(
      @Valid @RequestBody RoleUpdateStatusRequest request) {
    return roleUpdateStatusHandler.handle(request);
  }
}
