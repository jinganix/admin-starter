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

export class AuthorityActions {
  static async uploadUI(): Promise<boolean> {
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
    res && emitter.emit("error", ErrorCode.OK);
    return !!res;
  }

  static async reloadAPI(): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionReloadRequest.create(), PermissionReloadResponse);
    res && emitter.emit("error", ErrorCode.OK);
    return !!res;
  }
}
