import { startsWith } from "lodash";
import { makeAutoObservable } from "mobx";

export class AuthorityStore {
  private roles = new Set<string>();
  private authorities = new Set<string>();

  constructor() {
    makeAutoObservable(this);
  }

  dispose(): void {
    this.roles = new Set<string>();
    this.authorities = new Set<string>();
  }

  update(authorities: string[]): void {
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

export const authorityStore = new AuthorityStore();
