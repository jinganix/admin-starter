import { HttpService } from "@helpers/network/http.service.ts";
import { pbToPaging } from "@helpers/paging/pageable.ts";
import { DataLoader } from "@helpers/table/table.types.ts";
import {
  IPermissionEditPb,
  PermissionCreateRequest,
  PermissionCreateResponse,
  PermissionDeleteRequest,
  PermissionDeleteResponse,
  PermissionListRequest,
  PermissionListResponse,
  PermissionOptionsRequest,
  PermissionOptionsResponse,
  PermissionStatus,
  PermissionUpdateRequest,
  PermissionUpdateResponse,
  PermissionUpdateStatusRequest,
  PermissionUpdateStatusResponse,
} from "@proto/SysPermissionProto.ts";
import { container } from "tsyringe";
import {
  Permission,
  PermissionOption,
  PermissionQuery,
} from "@/sys/permission/permission.types.ts";

export class PermissionActions {
  static async getOptions(): Promise<PermissionOption[]> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionOptionsRequest.create(), PermissionOptionsResponse);
    return res?.options || [];
  }

  static list: DataLoader<PermissionQuery, Permission> = async (pageable, query) => {
    const res = await container
      .resolve(HttpService)
      .request(
        PermissionListRequest.create({ pageable, ...query, types: query.types || [] }),
        PermissionListResponse,
      );
    if (res) {
      return { paging: pbToPaging(res), records: res.records };
    }
    return null;
  };

  static async updateStatus(id: string, status: PermissionStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(
        PermissionUpdateStatusRequest.create({ id, status }),
        PermissionUpdateStatusResponse,
      );
    return !!res;
  }

  static async delete(ids: (string | undefined)[]): Promise<boolean> {
    const filtered = ids.filter((x) => x !== undefined);
    if (!filtered.length) {
      return false;
    }
    const res = await container
      .resolve(HttpService)
      .request(PermissionDeleteRequest.create({ ids: filtered }), PermissionDeleteResponse);
    return !!res;
  }

  static async update(id: string, pb: IPermissionEditPb): Promise<Permission | null> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionUpdateRequest.create({ id, ...pb }), PermissionUpdateResponse);
    return res ? res.permission : null;
  }

  static async create(pb: IPermissionEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionCreateRequest.create(pb), PermissionCreateResponse);
    return !!res;
  }
}
