import { HttpService } from "@helpers/network/http.service.ts";
import {
  UserChangePasswordRequest,
  UserChangePasswordResponse,
  UserRetrieveRequest,
  UserRetrieveResponse,
  UserUpdateProfileRequest,
  UserUpdateProfileResponse,
} from "@proto/SysUserProto.ts";
import { container } from "tsyringe";
import { authStore } from "@/sys/auth/auth.store.ts";
import { User } from "@/sys/user/user.ts";

export async function changePassword(current: string, password: string): Promise<boolean> {
  const res = await container
    .resolve(HttpService)
    .request(UserChangePasswordRequest.create({ current, password }), UserChangePasswordResponse);
  return !!res;
}

export async function updateProfile(nickname: string): Promise<boolean> {
  const res = await container
    .resolve(HttpService)
    .request(UserUpdateProfileRequest.create({ nickname }), UserUpdateProfileResponse);
  res?.user && authStore.update(res.user);
  return !!res;
}

export async function retrieveUser(id: string): Promise<User | null> {
  const res = await container
    .resolve(HttpService)
    .request(UserRetrieveRequest.create({ id }), UserRetrieveResponse);
  return res?.user ? User.ofDetailsPb(res.user) : null;
}
