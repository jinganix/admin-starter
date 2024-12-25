import { HttpService } from "@helpers/network/http.service.ts";
import { NetErrorHandler } from "@helpers/network/net.types.ts";
import { Replay } from "@helpers/network/replay.ts";
import { TokenService } from "@helpers/network/token.service.ts";
import { IUserPb, UserCurrentRequest, UserCurrentResponse, UserStatus } from "@proto/SysUserProto";
import { startsWith } from "lodash";
import { makeAutoObservable } from "mobx";
import { container } from "tsyringe";

export class AuthStore {
  replay = new Replay<void>();
  id = "";
  username = "";
  nickname = "";
  status: UserStatus | null = null;
  roles = new Set<string>();
  authorities = new Set<string>();

  constructor() {
    makeAutoObservable(this);
  }

  dispose(): void {
    this.replay = new Replay<void>();
    this.id = "";
    this.nickname = "";
    this.status = null;
    this.roles = new Set<string>();
    this.authorities = new Set<string>();
  }

  async initialize(errorHandler?: NetErrorHandler): Promise<void> {
    await this.replay.resolve(async () => {
      const token = await container.resolve(TokenService).getToken();
      token && (await this.loadCurrent(errorHandler));
    });
  }

  async loadCurrent(errorHandler?: NetErrorHandler): Promise<boolean> {
    const res = await container
      .resolve(HttpService)
      .request(UserCurrentRequest.create(), UserCurrentResponse, errorHandler);
    if (res?.user) {
      this.update(res.user);
      this.updateAuthorities(res.user.authorities);
    }
    return !!res;
  }

  isAuthed(): boolean {
    return !!this.id;
  }

  update(user: IUserPb): void {
    this.id = user.id;
    this.username = user.username;
    this.nickname = user.nickname;
    this.status = user.status;
  }

  updateAuthorities(authorities: string[]): void {
    const roles = new Set<string>();
    const permissions = new Set<string>();
    authorities.forEach((x) =>
      startsWith(x, "ROLE_") ? roles.add(x.substring("ROLE_".length)) : permissions.add(x),
    );
    this.roles = roles;
    this.authorities = permissions;
  }

  hasRole(role: string): boolean {
    return this.roles.has(role);
  }

  hasAuthority(authority: string): boolean {
    return this.authorities.has(authority);
  }
}

export const authStore = new AuthStore();
