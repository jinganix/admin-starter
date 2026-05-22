import { makeAutoObservable } from "mobx";
import { Cond, CondAnd, CondOr, CondType } from "@/helpers/condition/cond.types.ts";
import { authStore } from "@/sys/auth/auth.store.ts";

export class CondStore {
  constructor() {
    makeAutoObservable(this);
  }

  checkAnd(cond: CondAnd): boolean {
    for (const sub of cond.conds) {
      if (!this.satisfy(sub)) {
        return false;
      }
    }
    return true;
  }

  checkOr(cond: CondOr): boolean {
    for (const sub of cond.conds) {
      if (this.satisfy(sub)) {
        return true;
      }
    }
    return false;
  }

  checkCond(cond: Cond): boolean {
    switch (cond.type) {
      case CondType.not:
        return !this.satisfy(cond.cond);
      case CondType.and:
        return this.checkAnd(cond);
      case CondType.or:
        return this.checkOr(cond);
      case CondType.never:
        return false;
      case CondType.always:
        return true;
      case CondType.authed:
        return authStore.isAuthed();
      case CondType.hasRole:
        return authStore.hasRole(cond.role);
      case CondType.hasAuthority:
        return authStore.hasAuthority(cond.authority);
      default:
        return true;
    }
  }

  satisfy(cond?: Cond): boolean {
    return !cond ? true : this.checkCond(cond);
  }
}

export const condStore = new CondStore();
