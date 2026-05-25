import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { MonitorCogIcon, UsersIcon } from "lucide-react";
import { describe, expect, it, vi } from "vitest";
import { CollapsibleMenuItem } from "@/components/sidebar/collapsible.menu.item.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

vi.mock("@/components/shadcn/collapsible.tsx", () => ({
  CollapsibleContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  CollapsibleTrigger: ({ children, asChild }: { children: React.ReactNode; asChild?: boolean }) =>
    asChild ? children : <button type="button">{children}</button>,
}));

vi.mock("@/components/shadcn/sidebar.tsx", () => ({
  SidebarMenuButton: ({ children }: { children: React.ReactNode }) => (
    <button type="button">{children}</button>
  ),
  SidebarMenuItem: ({ children }: { children: React.ReactNode }) => <li>{children}</li>,
  SidebarMenuSub: ({ children }: { children: React.ReactNode }) => <ul>{children}</ul>,
}));

vi.mock("@/components/sidebar/collapsible.menu.sub.tsx", () => ({
  CollapsibleMenuSub: ({ menu }: { menu: MenuDef }) => <li>{menu.title}</li>,
}));

describe("<CollapsibleMenuItem />", () => {
  const menu: MenuDef = {
    icon: MonitorCogIcon,
    items: [{ icon: UsersIcon, title: "menu.system.users", url: "/users", visible: always() }],
    title: "menu.system.",
    url: "#",
  };

  it("should render collapsible menu title and sub items when mounted", () => {
    render(<CollapsibleMenuItem menu={menu} />);

    expect(screen.getByText("menu.system.")).toBeInTheDocument();
    expect(screen.getByText("menu.system.users")).toBeInTheDocument();
  });
});
