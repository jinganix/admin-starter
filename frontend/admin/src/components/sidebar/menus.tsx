import { Cond } from "@helpers/condition/cond.types.ts";
import {
  LayoutDashboardIcon,
  LockIcon,
  LogsIcon,
  type LucideIcon,
  MonitorCogIcon,
  UserRoundCheckIcon,
  UsersIcon,
} from "lucide-react";
import { CondRouteDef } from "@/components/condition/cond.route.tsx";
import { ROUTES } from "@/routes.tsx";

export interface MenuDef {
  title: string;
  url: string;
  visible?: Cond;
  icon?: LucideIcon;
  items?: MenuDef[];
}

function getPathCond(
  routes: CondRouteDef[],
  dir: string = "/",
  data: Record<string, Cond> = {},
): Record<string, Cond> {
  for (const route of routes) {
    const path = `${dir.replace(/\/+$/, "")}/${(route.path || "").replace(/^\/+/, "")}`;
    if ("routes" in route) {
      route.routes.forEach((x) => getPathCond([x], path, data));
    }
    path && route.cond && (data[path] = route.cond);
  }
  return data;
}

function fillVisible(menus: MenuDef[] = [], conds = getPathCond(ROUTES)): MenuDef[] {
  menus.forEach((x) => {
    x.visible = conds[x.url];
    x.items && fillVisible(x.items, conds);
  });
  return menus;
}

const menus: MenuDef[] = [
  {
    icon: LayoutDashboardIcon,
    title: "menu.dashboard",
    url: "/dashboard",
  },
  {
    icon: MonitorCogIcon,
    items: [
      {
        icon: LogsIcon,
        title: "menu.system.audits",
        url: "/audits",
      },
      {
        icon: UsersIcon,
        title: "menu.system.users",
        url: "/users",
      },
      {
        icon: UserRoundCheckIcon,
        title: "menu.system.roles",
        url: "/roles",
      },
      {
        icon: LockIcon,
        title: "menu.system.permissions",
        url: "/permissions",
      },
    ],
    title: "menu.system.",
    url: "#",
  },
];

let filledMenuDefs: MenuDef[] | null = null;

export function getMenuDefs(): MenuDef[] {
  if (!filledMenuDefs) {
    filledMenuDefs = fillVisible(menus);
  }
  return filledMenuDefs;
}
