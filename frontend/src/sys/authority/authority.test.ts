import { describe, expect, it } from "vitest";
import { Authority } from "@/sys/authority/authority.ts";

describe("Authority", () => {
  it("should expose menu and button authority codes", () => {
    expect(Authority.MENU).toBe("/menu/");
    expect(Authority.MENU_DASHBOARD).toBe("/menu/dashboard");
    expect(Authority.MENU_SYSTEM).toBe("/menu/system/");
    expect(Authority.MENU_AUDITS).toBe("/menu/system/audits");
    expect(Authority.MENU_USERS).toBe("/menu/system/users");
    expect(Authority.MENU_ROLES).toBe("/menu/system/roles");
    expect(Authority.MENU_PERMISSIONS).toBe("/menu/system/permissions");
    expect(Authority.BUTTON).toBe("/button/");
    expect(Authority.BTN_ADD_USER).toBe("/button/addUser");
    expect(Authority.BTN_ADD_PERMISSION).toBe("/button/addPermission");
  });

  it("should distinguish group menu paths from leaf ui paths", () => {
    expect(Authority.MENU.endsWith("/")).toBe(true);
    expect(Authority.MENU_DASHBOARD.endsWith("/")).toBe(false);
  });
});
