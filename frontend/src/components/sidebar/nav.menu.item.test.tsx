import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { LayoutDashboardIcon } from "lucide-react";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { NavMenuItem } from "@/components/sidebar/nav.menu.item.tsx";

const setOpenMobile = vi.fn();

vi.mock("@/components/shadcn/sidebar.tsx", () => ({
  SidebarMenuButton: ({ children, asChild }: { children: React.ReactNode; asChild?: boolean }) =>
    asChild ? children : <button type="button">{children}</button>,
  SidebarMenuItem: ({ children }: { children: React.ReactNode }) => <li>{children}</li>,
  useSidebar: () => ({ setOpenMobile }),
}));

describe("<NavMenuItem />", () => {
  const menu: MenuDef = {
    icon: LayoutDashboardIcon,
    title: "menu.dashboard",
    url: "/dashboard",
    visible: always(),
  };

  afterEach(() => vi.clearAllMocks());

  it("should render menu link when mounted", () => {
    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <NavMenuItem menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.getByRole("link", { name: /menu.dashboard/i })).toHaveAttribute(
      "href",
      "/dashboard",
    );
  });

  it("should close mobile sidebar when link is clicked", async () => {
    render(
      <MemoryRouter initialEntries={["/"]}>
        <NavMenuItem menu={menu} />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("link", { name: /menu.dashboard/i }));

    expect(setOpenMobile).toHaveBeenCalledWith(false);
  });
});
