import { emitter } from "@helpers/event/emitter.ts";
import { HttpService } from "@helpers/network/http.service.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import {
  PermissionReloadRequest,
  PermissionReloadResponse,
  PermissionStatus,
  PermissionType,
  PermissionUploadRequest,
  PermissionUploadResponse,
} from "@proto/SysPermissionProto.ts";
import { replace } from "lodash";
import { container } from "tsyringe";
import { Authority } from "@/sys/authority/authority.ts";
import { permissionsStore } from "@/sys/permission/permissions.store.ts";

export async function uploadAuthorities(): Promise<void> {
  const permissions = Object.values(Authority).map((x) => {
    return {
      code: x,
      description: "",
      name: `authority${replace(x, /\//g, ".")}`,
      status: PermissionStatus.ACTIVE,
      type: x.endsWith(".") ? PermissionType.GROUP : PermissionType.UI,
    };
  });
  const res = await container
    .resolve(HttpService)
    .request(PermissionUploadRequest.create({ permissions }), PermissionUploadResponse);
  if (res) {
    await permissionsStore.reload();
    emitter.emit("error", ErrorCode.OK);
  }
}

export async function reloadAuthorities(): Promise<void> {
  const res = await container
    .resolve(HttpService)
    .request(PermissionReloadRequest.create(), PermissionReloadResponse);
  if (res) {
    await permissionsStore.reload();
    emitter.emit("error", ErrorCode.OK);
  }
}
