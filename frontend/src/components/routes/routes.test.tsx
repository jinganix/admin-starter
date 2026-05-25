import { describe, expect, it } from "vitest";
import { CondLayoutRouteDef, CondRouteDef } from "@/components/condition/cond.route.tsx";
import { ROUTES } from "@/components/routes/routes.tsx";

function childRoutes(route: CondRouteDef | undefined): CondRouteDef[] {
  return route && "routes" in route ? route.routes : [];
}

function findChildRoute(routes: CondRouteDef[], path: string): CondRouteDef | undefined {
  return routes.find((route) => "path" in route && route.path === path);
}

describe("ROUTES", () => {
  it("should define authed layout with dashboard route", () => {
    const authedLayout = ROUTES.find((route) => route.path === "/") as
      | CondLayoutRouteDef
      | undefined;
    const dashboard = findChildRoute(childRoutes(authedLayout), "/dashboard");

    expect(authedLayout?.element).toBeTruthy();
    expect(dashboard?.path).toBe("/dashboard");
    expect(dashboard?.cond).toBeTruthy();
  });

  it("should define public auth routes with login redirect", () => {
    const publicLayout = ROUTES.find((route) => !route.path) as CondLayoutRouteDef | undefined;
    const login = findChildRoute(childRoutes(publicLayout), "/login");

    expect(login?.path).toBe("/login");
    expect(login?.redirects?.[0]?.path).toBe("/");
  });

  it("should define forbidden route for authed users", () => {
    const publicLayout = ROUTES.find((route) => !route.path) as CondLayoutRouteDef | undefined;
    const forbidden = findChildRoute(childRoutes(publicLayout), "/403");

    expect(forbidden?.path).toBe("/403");
    expect(forbidden?.redirects?.[0]?.path).toBe("/login");
  });
});
