import { describe, expect, it } from "vitest";
import { getMenuDefs } from "@/components/sidebar/menus.tsx";

describe("getMenuDefs", () => {
  it("should return menus with visible conditions from routes", () => {
    const menus = getMenuDefs();

    expect(menus.length).toBeGreaterThan(0);
    expect(menus[0].url).toBe("/dashboard");
    expect(menus[0].visible).toBeTruthy();
    expect(menus[1].items?.every((item) => item.visible)).toBe(true);
  });

  it("should return cached menu defs on subsequent calls", () => {
    const first = getMenuDefs();
    const second = getMenuDefs();

    expect(first).toBe(second);
  });
});
