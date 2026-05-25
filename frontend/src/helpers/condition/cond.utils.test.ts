import { describe, expect, it } from "vitest";
import { CondType } from "@/helpers/condition/cond.types.ts";
import {
  always,
  and,
  hasAuthority,
  hasRole,
  isAdmin,
  isAuthed,
  never,
  not,
  or,
} from "@/helpers/condition/cond.utils.ts";

describe("cond.utils", () => {
  it("should build primitive condition nodes", () => {
    expect(never()).toEqual({ type: CondType.never });
    expect(always()).toEqual({ type: CondType.always });
    expect(isAuthed()).toEqual({ type: CondType.authed });
  });

  it("should build role and authority conditions", () => {
    expect(hasRole("ADMIN")).toEqual({ role: "ADMIN", type: CondType.hasRole });
    expect(hasAuthority("USER_READ")).toEqual({
      authority: "USER_READ",
      type: CondType.hasAuthority,
    });
    expect(isAdmin()).toEqual({ role: "ADMIN", type: CondType.hasRole });
  });

  it("should compose and or and not conditions", () => {
    const authed = isAuthed();
    expect(and(authed, always())).toEqual({ conds: [authed, always()], type: CondType.and });
    expect(or(authed, never())).toEqual({ conds: [authed, never()], type: CondType.or });
    expect(not(authed)).toEqual({ cond: authed, type: CondType.not });
  });
});
