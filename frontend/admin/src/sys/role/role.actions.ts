import { HttpService } from "@helpers/network/http.service.ts";
import { Option } from "@helpers/option.ts";
import { RoleOptionsRequest, RoleOptionsResponse } from "@proto/SysRoleProto.ts";
import { container } from "tsyringe";

export async function getRoleOptions(): Promise<Option<string>[]> {
  const res = await container
    .resolve(HttpService)
    .request(RoleOptionsRequest.create(), RoleOptionsResponse);
  return res?.options || [];
}
