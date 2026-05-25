import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { AppSidebar } from "@/components/sidebar/app.sidebar.tsx";

vi.mock("@/components/shadcn/sidebar", () => ({
  Sidebar: ({ children, collapsible }: { children: React.ReactNode; collapsible?: string }) => (
    <aside data-collapsible={collapsible}>{children}</aside>
  ),
  SidebarContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

vi.mock("@/components/sidebar/menus.tsx", () => ({
  getMenuDefs: () => [{ title: "menu.dashboard", url: "/dashboard" }],
}));

vi.mock("@/components/sidebar/nav.menu.tsx", () => ({
  NavMenu: ({ menus }: { menus: { title: string }[] }) => (
    <nav>{menus.map((menu) => menu.title).join(",")}</nav>
  ),
}));

describe("<AppSidebar />", () => {
  it("should render nav menu with menu defs when mounted", () => {
    render(<AppSidebar className="test-sidebar" />);

    expect(screen.getByText("menu.dashboard")).toBeInTheDocument();
    expect(document.querySelector('[data-collapsible="icon"]')).toBeInTheDocument();
  });
});
