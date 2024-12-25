import { HttpService } from "@helpers/network/http.service.ts";
import { PermissionOptionsRequest, PermissionOptionsResponse } from "@proto/SysPermissionProto.ts";
import { container } from "tsyringe";
import { PermissionOption } from "@/sys/permission/permission.types.ts";

export async function getPermissionOptions(): Promise<PermissionOption[]> {
  const res = await container
    .resolve(HttpService)
    .request(PermissionOptionsRequest.create(), PermissionOptionsResponse);
  return res?.options || [];
}
