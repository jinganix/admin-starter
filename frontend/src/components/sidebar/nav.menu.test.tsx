import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { LayoutDashboardIcon, MonitorCogIcon, UsersIcon } from "lucide-react";
import { describe, expect, it, vi } from "vitest";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { NavMenu } from "@/components/sidebar/nav.menu.tsx";

const satisfy = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy }),
}));

vi.mock("@/components/shadcn/sidebar", () => ({
  SidebarGroup: ({ children }: { children: React.ReactNode }) => <section>{children}</section>,
  SidebarGroupLabel: ({ children }: { children: React.ReactNode }) => <h2>{children}</h2>,
  SidebarMenu: ({ children }: { children: React.ReactNode }) => <ul>{children}</ul>,
  useSidebar: () => ({ state: "expanded" }),
}));

vi.mock("@/components/sidebar/nav.menu.item.tsx", () => ({
  NavMenuItem: ({ menu }: { menu: MenuDef }) => <li>{menu.title}</li>,
}));

vi.mock("@/components/sidebar/collapsible.menu.tsx", () => ({
  CollapsibleMenu: ({ menu }: { menu: MenuDef }) => <li>{menu.title}-collapsible</li>,
}));

vi.mock("@/components/sidebar/sidebar.dropdown.menu.tsx", () => ({
  SidebarDropdownMenu: ({ menu }: { menu: MenuDef }) => <li>{menu.title}-dropdown</li>,
}));

describe("<NavMenu />", () => {
  const menus: MenuDef[] = [
    { icon: LayoutDashboardIcon, title: "menu.dashboard", url: "/dashboard", visible: always() },
    {
      icon: MonitorCogIcon,
      items: [{ icon: UsersIcon, title: "menu.system.users", url: "/users", visible: always() }],
      title: "menu.system.",
      url: "#",
      visible: always(),
    },
  ];

  it("should render visible menu items when sidebar is expanded", () => {
    satisfy.mockReturnValue(true);

    render(<NavMenu menus={menus} />);

    expect(screen.getByText("Admin Starter")).toBeInTheDocument();
    expect(screen.getByText("menu.dashboard")).toBeInTheDocument();
    expect(screen.getByText("menu.system.-collapsible")).toBeInTheDocument();
  });
});
