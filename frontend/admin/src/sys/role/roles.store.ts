import { HttpService } from "@helpers/network/http.service.ts";
import { Pageable } from "@helpers/paging/pageable.ts";
import { Paging } from "@helpers/paging/paging.ts";
import {
  IRoleEditPb,
  IRoleListRequest,
  RoleCreateRequest,
  RoleCreateResponse,
  RoleDeleteRequest,
  RoleDeleteResponse,
  RoleListRequest,
  RoleListResponse,
  RoleStatus,
  RoleUpdateRequest,
  RoleUpdateResponse,
  RoleUpdateStatusRequest,
  RoleUpdateStatusResponse,
} from "@proto/SysRoleProto.ts";
import { isEqual } from "lodash";
import { makeAutoObservable, runInAction } from "mobx";
import { container } from "tsyringe";
import { Role } from "@/sys/role/role.ts";

export class RolesStore {
  private query?: IRoleListRequest;
  private loadedAt?: number;
  paging = new Paging();
  records: Role[] = [];

  constructor() {
    makeAutoObservable(this);
  }

  private checkSkip(query?: IRoleListRequest): boolean {
    if (isEqual(this.query, query) && (this.loadedAt ?? 0) + 100 > Date.now()) {
      return true;
    }
    this.query = query;
    this.loadedAt = Date.now();
    return false;
  }

  async load(pageable: Pageable, name?: string, status?: RoleStatus): Promise<void> {
    if (!this.checkSkip({ name, pageable, status })) {
      await this.reload();
    }
  }

  async reload(): Promise<void> {
    const res = await container
      .resolve(HttpService)
      .request(RoleListRequest.create(this.query), RoleListResponse);
    if (res) {
      runInAction(() => {
        this.paging = Paging.ofPb(res);
        this.records = res.records.map((x) => Role.ofPb(x));
      });
    }
  }

  async toggleStatus(id: string, status: RoleStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(RoleUpdateStatusRequest.create({ id, status }), RoleUpdateStatusResponse);
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
      .request(RoleDeleteRequest.create({ ids }), RoleDeleteResponse);
    res && (await this.reload());
    return !!res;
  }

  async update(id: string, pb: IRoleEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(RoleUpdateRequest.create({ id, ...pb }), RoleUpdateResponse);
    if (res) {
      runInAction(() => {
        const role = Role.ofPb(res.role);
        this.records = this.records.map((x) => (x.id === role.id ? role : x));
      });
    }
    return !!res;
  }

  async create(pb: IRoleEditPb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(RoleCreateRequest.create(pb), RoleCreateResponse);
    if (res) {
      await this.reload();
    }
    return !!res;
  }
}

export const rolesStore = new RolesStore();
