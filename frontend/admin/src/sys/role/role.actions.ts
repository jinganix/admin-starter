import { HttpService } from "@helpers/network/http.service.ts";
import { Option } from "@helpers/option.ts";
import { pbToPaging } from "@helpers/paging/pageable.ts";
import { DataLoader } from "@helpers/table/table.types.ts";
import {
  IRoleEditPb,
  RoleCreateRequest,
  RoleCreateResponse,
  RoleDeleteRequest,
  RoleDeleteResponse,
  RoleListRequest,
  RoleListResponse,
  RoleOptionsRequest,
  RoleOptionsResponse,
  RoleStatus,
  RoleUpdateRequest,
  RoleUpdateResponse,
  RoleUpdateStatusRequest,
  RoleUpdateStatusResponse,
} from "@proto/SysRoleProto.ts";
import { container } from "tsyringe";
import { Role, RoleQuery } from "@/sys/role/role.types.ts";

export class RoleActions {
  static async getOptions(): Promise<Option<string>[]> {
    const res = await container
      .resolve(HttpService)
      .request(RoleOptionsRequest.create(), RoleOptionsResponse);
    return res?.options || [];
  }

  static list: DataLoader<RoleQuery, Role> = async (pageable, query) => {
    const res = await container
      .resolve(HttpService)
      .request(RoleListRequest.create({ pageable, ...query }), RoleListResponse);
    if (res) {
      return { paging: pbToPaging(res), records: res.records };
    }
    return null;
  };

  static async updateStatus(id: string, status: RoleStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(RoleUpdateStatusRequest.create({ id, status }), RoleUpdateStatusResponse);
    return !!res;
  }

  static async delete(ids: (string | undefined)[]): Promise<boolean> {
    const filtered = ids.filter((x) => x !== undefined);
    if (!filtered.length) {
      return false;
    }
    const res = await container
      .resolve(HttpService)
      .request(RoleDeleteRequest.create({ ids: filtered }), RoleDeleteResponse);
    return !!res;
  }

  static async update(id: string, pb: IRoleEditPb): Promise<Role | null> {
    const res = await container
      .resolve(HttpService)
      .request(RoleUpdateRequest.create({ id, ...pb }), RoleUpdateResponse);
    return res ? res.role : null;
  }

  static async create(pb: IRoleEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(RoleCreateRequest.create(pb), RoleCreateResponse);
    return !!res;
  }
}
