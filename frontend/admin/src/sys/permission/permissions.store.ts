import { HttpService } from "@helpers/network/http.service.ts";
import { Pageable } from "@helpers/paging/pageable.ts";
import { Paging } from "@helpers/paging/paging.ts";
import {
  IPermissionEditPb,
  IPermissionListRequest,
  PermissionCreateRequest,
  PermissionCreateResponse,
  PermissionDeleteRequest,
  PermissionDeleteResponse,
  PermissionListRequest,
  PermissionListResponse,
  PermissionStatus,
  PermissionType,
  PermissionUpdateRequest,
  PermissionUpdateResponse,
  PermissionUpdateStatusRequest,
  PermissionUpdateStatusResponse,
} from "@proto/SysPermissionProto.ts";
import { isEqual } from "lodash";
import { makeAutoObservable, runInAction } from "mobx";
import { container } from "tsyringe";
import { Permission } from "@/sys/permission/permission.ts";

export class PermissionsStore {
  private query?: IPermissionListRequest;
  private loadedAt?: number;
  paging = new Paging();
  records: Permission[] = [];

  constructor() {
    makeAutoObservable(this);
  }

  private checkSkip(query?: IPermissionListRequest): boolean {
    if (isEqual(this.query, query) && (this.loadedAt ?? 0) + 100 > Date.now()) {
      return true;
    }
    this.query = query;
    this.loadedAt = Date.now();
    return false;
  }

  async load(
    pageable: Pageable,
    code?: string,
    status?: PermissionStatus,
    types: PermissionType[] = [],
  ): Promise<boolean> {
    if (this.checkSkip({ code, pageable, status, types })) {
      return true;
    }
    return await this.reload();
  }

  async reload(): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionListRequest.create(this.query), PermissionListResponse);
    runInAction(() => {
      if (res) {
        this.paging = Paging.ofPb(res);
        this.records = res.records.map((x) => Permission.ofPb(x));
      } else {
        this.records = [];
      }
    });
    return !!res;
  }

  async toggleStatus(id: string, status: PermissionStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(
        PermissionUpdateStatusRequest.create({ id, status }),
        PermissionUpdateStatusResponse,
      );
    if (res) {
      runInAction(
        () => (this.records = this.records.map((x) => (x.id === id ? x.setStatus(status) : x))),
      );
    }
    return !!res;
  }

  async delete(ids: string[]): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionDeleteRequest.create({ ids }), PermissionDeleteResponse);
    res && (await this.reload());
    return !!res;
  }

  async update(id: string, pb: IPermissionEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionUpdateRequest.create({ id, ...pb }), PermissionUpdateResponse);
    if (res) {
      const permission = Permission.ofPb(res.permission);
      this.records = this.records.map((x) => (x.id === permission.id ? permission : x));
    }
    return !!res;
  }

  async create(pb: IPermissionEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(PermissionCreateRequest.create(pb), PermissionCreateResponse);
    res && (await this.reload());
    return !!res;
  }
}

export const permissionsStore = new PermissionsStore();
