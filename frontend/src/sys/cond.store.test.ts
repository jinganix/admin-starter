import { afterEach, describe, expect, it, vi } from "vitest";
import { CondType } from "@/helpers/condition/cond.types.ts";
import { authStore } from "@/sys/auth/auth.store.ts";
import { CondStore } from "@/sys/cond.store.ts";

describe("CondStore", () => {
  const store = new CondStore();

  afterEach(() => {
    authStore.dispose();
    vi.restoreAllMocks();
  });

  it("should return true when cond is undefined", () => {
    expect(store.satisfy(undefined)).toBe(true);
  });

  it("should evaluate and cond as false when any child fails", () => {
    vi.spyOn(authStore, "isAuthed").mockReturnValue(true);
    vi.spyOn(authStore, "hasRole").mockReturnValue(false);

    expect(
      store.satisfy({
        conds: [{ type: CondType.authed }, { role: "ADMIN", type: CondType.hasRole }],
        type: CondType.and,
      }),
    ).toBe(false);
  });

  it("should evaluate or cond as true when any child passes", () => {
    vi.spyOn(authStore, "hasAuthority").mockReturnValueOnce(false).mockReturnValueOnce(true);

    expect(
      store.satisfy({
        conds: [
          { authority: "X", type: CondType.hasAuthority },
          { authority: "Y", type: CondType.hasAuthority },
        ],
        type: CondType.or,
      }),
    ).toBe(true);
  });

  it("should evaluate not never and always", () => {
    vi.spyOn(authStore, "isAuthed").mockReturnValue(true);

    expect(store.satisfy({ cond: { type: CondType.authed }, type: CondType.not })).toBe(false);
    expect(store.satisfy({ type: CondType.never })).toBe(false);
    expect(store.satisfy({ type: CondType.always })).toBe(true);
  });

  it("should evaluate and cond as true when all children pass", () => {
    vi.spyOn(authStore, "isAuthed").mockReturnValue(true);
    vi.spyOn(authStore, "hasRole").mockReturnValue(true);

    expect(
      store.satisfy({
        conds: [{ type: CondType.authed }, { role: "ADMIN", type: CondType.hasRole }],
        type: CondType.and,
      }),
    ).toBe(true);
  });

  it("should evaluate or cond as false when all children fail", () => {
    vi.spyOn(authStore, "hasAuthority").mockReturnValue(false);

    expect(
      store.satisfy({
        conds: [
          { authority: "X", type: CondType.hasAuthority },
          { authority: "Y", type: CondType.hasAuthority },
        ],
        type: CondType.or,
      }),
    ).toBe(false);
  });

  it("should delegate authed role and authority to auth store", () => {
    vi.spyOn(authStore, "isAuthed").mockReturnValue(true);
    vi.spyOn(authStore, "hasRole").mockReturnValue(true);
    vi.spyOn(authStore, "hasAuthority").mockReturnValue(true);

    expect(store.satisfy({ type: CondType.authed })).toBe(true);
    expect(store.satisfy({ role: "ADMIN", type: CondType.hasRole })).toBe(true);
    expect(store.satisfy({ authority: "USER_READ", type: CondType.hasAuthority })).toBe(true);
  });
});
