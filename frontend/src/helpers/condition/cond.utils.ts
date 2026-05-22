import { Cond, CondType } from "@/helpers/condition/cond.types.ts";

export function never(): Cond {
  return { type: CondType.never };
}

export function always(): Cond {
  return { type: CondType.always };
}

export function hasAuthority(authority: string): Cond {
  return { authority, type: CondType.hasAuthority };
}

export function hasRole(role: string): Cond {
  return { role, type: CondType.hasRole };
}

export function isAdmin(): Cond {
  return hasRole("ADMIN");
}

export function isAuthed(): Cond {
  return { type: CondType.authed };
}

export function and(...conds: Cond[]): Cond {
  return { conds, type: CondType.and };
}

export function or(...conds: Cond[]): Cond {
  return { conds, type: CondType.or };
}

export function not(cond: Cond): Cond {
  return { cond, type: CondType.not };
}
