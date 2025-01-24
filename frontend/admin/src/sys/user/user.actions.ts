import { HttpService } from "@helpers/network/http.service.ts";
import { pbToPaging } from "@helpers/paging/pageable.ts";
import { DataLoader } from "@helpers/table/table.types.ts";
import {
  IUserCreatePb,
  IUserUpdatePb,
  UserChangePasswordRequest,
  UserChangePasswordResponse,
  UserCreateRequest,
  UserCreateResponse,
  UserDeleteRequest,
  UserDeleteResponse,
  UserListRequest,
  UserListResponse,
  UserRetrieveRequest,
  UserRetrieveResponse,
  UserStatus,
  UserUpdateProfileRequest,
  UserUpdateProfileResponse,
  UserUpdateRequest,
  UserUpdateResponse,
  UserUpdateStatusRequest,
  UserUpdateStatusResponse,
} from "@proto/SysUserProto.ts";
import { container } from "tsyringe";
import { authStore } from "@/sys/auth/auth.store.ts";
import { User, UserQuery } from "@/sys/user/user.types.ts";

export class UserActions {
  static list: DataLoader<UserQuery, User> = async (pageable, query) => {
    const res = await container
      .resolve(HttpService)
      .request(UserListRequest.create({ pageable, ...query }), UserListResponse);
    if (res) {
      return { paging: pbToPaging(res), records: res.records };
    }
    return null;
  };

  static async updateStatus(id: string, status: UserStatus): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserUpdateStatusRequest.create({ id, status }), UserUpdateStatusResponse);
    return !!res;
  }

  static async delete(ids: (string | undefined)[]): Promise<boolean> {
    const filtered = ids.filter((x) => x !== undefined);
    if (!filtered.length) {
      return false;
    }
    const res = await container
      .resolve(HttpService)
      .request(UserDeleteRequest.create({ ids: filtered }), UserDeleteResponse);
    return !!res;
  }

  static async update(id: string, pb: IUserUpdatePb): Promise<User | null> {
    const res = await container
      .resolve(HttpService)
      .request(UserUpdateRequest.create({ id, ...pb }), UserUpdateResponse);
    return res ? res.user : null;
  }

  static async create(pb: IUserCreatePb): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserCreateRequest.create(pb), UserCreateResponse);
    return !!res;
  }

  static async changePassword(current: string, password: string): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserChangePasswordRequest.create({ current, password }), UserChangePasswordResponse);
    return !!res;
  }

  static async updateProfile(nickname: string): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserUpdateProfileRequest.create({ nickname }), UserUpdateProfileResponse);
    res?.user && authStore.update(res.user);
    return !!res;
  }

  static async retrieve(id: string): Promise<User | null> {
    const res = await container
      .resolve(HttpService)
      .request(UserRetrieveRequest.create({ id }), UserRetrieveResponse);
    return res?.user ? res.user : null;
  }
}
