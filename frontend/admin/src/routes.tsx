import { Cond } from "@helpers/condition/cond.types.ts";
import {
  always,
  hasAuthority,
  isAdmin,
  isAuthed,
  never,
  not,
  or,
} from "@helpers/condition/cond.utils.ts";
import { ReactElement, ReactNode } from "react";
import { Route } from "react-router";
import { Navigate } from "react-router-dom";
import { CondRoute, CondRouteDef } from "@/components/condition/cond.route.tsx";
import { AuthedPageLayout } from "@/components/layout/authed.page.layout.tsx";
import { PageLayout } from "@/components/layout/page.layout.tsx";
import { AuditsPage } from "@/pages/audits.page.tsx";
import { DashboardPage } from "@/pages/dashboard.page.tsx";
import { Error403 } from "@/pages/error/403.tsx";
import { LoginPage } from "@/pages/login.page.tsx";
import { PermissionsPage } from "@/pages/permissions.page.tsx";
import { RolesPage } from "@/pages/roles.page.tsx";
import { SettingsCredentialPage } from "@/pages/settings/settings.credential.page.tsx";
import { SettingsLayout } from "@/pages/settings/settings.layout.tsx";
import { SettingsProfilePage } from "@/pages/settings/settings.profile.page.tsx";
import { SignupPage } from "@/pages/signup.page.tsx";
import { UsersPage } from "@/pages/users.page.tsx";
import { Authority } from "@/sys/authority/authority.ts";

export function toRoutes(routes: CondRouteDef[] = []): ReactNode[] {
  return routes.map((props) => {
    if (!props.cond) {
      if ("routes" in props) {
        return <Route {...props}>{...toRoutes(props.routes)}</Route>;
      }
      return <Route {...props} />;
    }
    if ("routes" in props) {
      return (
        <Route {...props} element={<CondRoute {...props} />}>
          {...toRoutes(props.routes)}
        </Route>
      );
    } else {
      return <Route {...props} element={<CondRoute {...props} />} />;
    }
  });
}

function cond(path: string, cond: Cond, element: ReactElement): CondRouteDef {
  return { cond, element, path, redirects: [{ cond: not(isAuthed()), path: "/login" }] };
}

export const ROUTES: CondRouteDef[] = [
  {
    cond: never(),
    element: <AuthedPageLayout />,
    path: "/",
    redirects: [{ cond: always(), path: "/dashboard" }],
    routes: [
      {
        cond: never(),
        element: <SettingsLayout />,
        path: "/settings",
        redirects: [{ cond: always(), path: "/settings/profile" }],
        routes: [
          cond("/settings/profile", isAuthed(), <SettingsProfilePage />),
          cond("/settings/credential", isAuthed(), <SettingsCredentialPage />),
        ],
      },
      cond("/dashboard", or(isAdmin(), hasAuthority(Authority.MENU_DASHBOARD)), <DashboardPage />),
      cond("/audits", hasAuthority(Authority.MENU_AUDITS), <AuditsPage />),
      cond("/users", hasAuthority(Authority.MENU_USERS), <UsersPage />),
      cond("/roles", hasAuthority(Authority.MENU_ROLES), <RolesPage />),
      cond(
        "/permissions",
        or(isAdmin(), hasAuthority(Authority.MENU_PERMISSIONS)),
        <PermissionsPage />,
      ),
    ],
  },
  {
    element: <PageLayout />,
    routes: [
      {
        cond: not(isAuthed()),
        element: <LoginPage />,
        path: "/login",
        redirects: [{ cond: isAuthed(), path: "/" }],
      },
      {
        cond: not(isAuthed()),
        element: <SignupPage />,
        path: "/signup",
        redirects: [{ cond: isAuthed(), path: "/" }],
      },
      {
        cond: isAuthed(),
        element: <Error403 />,
        path: "/403",
        redirects: [{ cond: not(isAuthed()), path: "/login" }],
      },
      {
        element: <Navigate to="/dashboard" />,
        path: "*",
      },
    ],
  },
];

export function getRoutes(): ReactNode[] {
  return toRoutes(ROUTES);
}
