export enum CondType {
  not = 0,
  and,
  or,
  never,
  always,
  authed,
  hasAuthority,
  hasRole,
}

export interface BaseCond<T extends CondType> {
  type: T;
}

export interface CondNot extends BaseCond<CondType.not> {
  cond: Cond;
}

export interface CondAnd extends BaseCond<CondType.and> {
  conds: Cond[];
}

export interface CondOr extends BaseCond<CondType.or> {
  conds: Cond[];
}

export type CondNever = BaseCond<CondType.never>;

export type CondAlways = BaseCond<CondType.always>;

export type CondAuthed = BaseCond<CondType.authed>;

export interface CondHasAuthority extends BaseCond<CondType.hasAuthority> {
  authority: string;
}

export interface CondHasRole extends BaseCond<CondType.hasRole> {
  role: string;
}

export type Cond =
  | CondNot
  | CondAnd
  | CondOr
  | CondNever
  | CondAlways
  | CondAuthed
  | CondHasAuthority
  | CondHasRole;
