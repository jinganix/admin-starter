package io.github.jinganix.admin.starter.sys.permission;

import io.github.jinganix.admin.starter.helper.actor.OrderedTraceExecutor;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionCreateResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionDeleteResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionListResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionOptionsResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionReloadResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionRetrieveResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUpdateStatusResponse;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadRequest;
import io.github.jinganix.admin.starter.proto.sys.permission.PermissionUploadResponse;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionCreateHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionDeleteHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionListHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionOptionsHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionReloadHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionRetrieveHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionUpdateHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionUpdateStatusHandler;
import io.github.jinganix.admin.starter.sys.permission.handler.PermissionUploadHandler;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PermissionController {

  private final OrderedTraceExecutor orderedTraceExecutor;

  private final PermissionCreateHandler permissionCreateHandler;

  private final PermissionDeleteHandler permissionDeleteHandler;

  private final PermissionListHandler permissionListHandler;

  private final PermissionOptionsHandler permissionOptionsHandler;

  private final PermissionReloadHandler permissionReloadHandler;

  private final PermissionUploadHandler permissionUploadHandler;

  private final PermissionRetrieveHandler permissionRetrieveHandler;

  private final PermissionUpdateHandler permissionUpdateHandler;

  private final PermissionUpdateStatusHandler permissionUpdateStatusHandler;

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_CREATE)")
  @WebpbRequestMapping
  public PermissionCreateResponse create(@Valid @RequestBody PermissionCreateRequest request) {
    return orderedTraceExecutor.supply(
        "PERMISSION_CREATE", () -> permissionCreateHandler.handle(request));
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_DELETE)")
  @WebpbRequestMapping
  public PermissionDeleteResponse delete(@Valid @RequestBody PermissionDeleteRequest request) {
    return orderedTraceExecutor.supply(
        "PERMISSION_DELETE", () -> permissionDeleteHandler.handle(request));
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_LIST)")
  @WebpbRequestMapping
  public PermissionListResponse list(Pageable pageable, @Valid PermissionListRequest request) {
    return permissionListHandler.handle(pageable, request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_RELOAD)")
  @WebpbRequestMapping(message = PermissionReloadRequest.class)
  public PermissionReloadResponse reload() {
    return orderedTraceExecutor.supply("PERMISSION_RELOAD", permissionReloadHandler::handle);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_OPTIONS)")
  @WebpbRequestMapping(message = PermissionOptionsRequest.class)
  public PermissionOptionsResponse options() {
    return permissionOptionsHandler.handle();
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_LIST)")
  @WebpbRequestMapping
  public PermissionRetrieveResponse retrieve(@Valid PermissionRetrieveRequest request) {
    return permissionRetrieveHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_UPDATE)")
  @WebpbRequestMapping
  public PermissionUpdateResponse update(@Valid @RequestBody PermissionUpdateRequest request) {
    return permissionUpdateHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_STATUS)")
  @WebpbRequestMapping
  public PermissionUpdateStatusResponse updateStatus(
      @Valid @RequestBody PermissionUpdateStatusRequest request) {
    return permissionUpdateStatusHandler.handle(request);
  }

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).SYS_PERMISSION_UPLOAD)")
  @WebpbRequestMapping
  public PermissionUploadResponse upload(@Valid @RequestBody PermissionUploadRequest request) {
    return orderedTraceExecutor.supply(
        "PERMISSION_UPLOAD", () -> permissionUploadHandler.handle(request));
  }
}
