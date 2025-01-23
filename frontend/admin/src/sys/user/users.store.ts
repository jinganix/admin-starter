import { HttpService } from "@helpers/network/http.service.ts";
import { Pageable } from "@helpers/paging/pageable.ts";
import { Paging } from "@helpers/paging/paging.ts";
import {
  IUserCreateRequest,
  IUserListRequest,
  IUserUpdateRequest,
  UserCreateRequest,
  UserCreateResponse,
  UserDeleteRequest,
  UserDeleteResponse,
  UserListRequest,
  UserListResponse,
  UserStatus,
  UserUpdateRequest,
  UserUpdateResponse,
  UserUpdateStatusRequest,
  UserUpdateStatusResponse,
} from "@proto/SysUserProto.ts";
import { isEqual } from "lodash";
import { makeAutoObservable, runInAction } from "mobx";
import { container } from "tsyringe";
import { User } from "@/sys/user/user.ts";

export class UsersStore {
  private query?: IUserListRequest;
  private loadedAt?: number;
  paging = new Paging();
  records: User[] = [];

  constructor() {
    makeAutoObservable(this);
  }

  private checkSkip(query?: IUserListRequest): boolean {
    if (isEqual(this.query, query) && (this.loadedAt ?? 0) + 100 > Date.now()) {
      return true;
    }
    this.query = query;
    this.loadedAt = Date.now();
    return false;
  }

  async load(pageable: Pageable, username?: string, status?: UserStatus): Promise<void> {
    if (!this.checkSkip({ pageable, status, username })) {
      await this.reload();
    }
  }

  async reload(): Promise<void> {
    const res = await container
      .resolve(HttpService)
      .request(UserListRequest.create(this.query), UserListResponse);
    if (res) {
      runInAction(() => {
        this.paging = Paging.ofPb(res);
        this.records = res.records.map((x) => User.ofPb(x));
      });
    }
  }

  async toggleStatus(id: string, status: UserStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserUpdateStatusRequest.create({ id, status }), UserUpdateStatusResponse);
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
      .request(UserDeleteRequest.create({ ids }), UserDeleteResponse);
    res && (await this.reload());
    return !!res;
  }

  async update(pb: IUserUpdateRequest): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserUpdateRequest.create(pb), UserUpdateResponse);
    if (res) {
      runInAction(() => {
        const user = User.ofPb(res.user);
        this.records = this.records.map((x) => (x.id === user.id ? user : x));
      });
    }
    return !!res;
  }

  async create(pb: IUserCreateRequest): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserCreateRequest.create(pb), UserCreateResponse);
    if (res) {
      await this.reload();
    }
    return !!res;
  }
}

export const usersStore = new UsersStore();
