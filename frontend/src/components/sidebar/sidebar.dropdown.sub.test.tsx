import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { UsersIcon } from "lucide-react";
import { MemoryRouter } from "react-router";
import { describe, expect, it, vi } from "vitest";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { SidebarDropdownSub } from "@/components/sidebar/sidebar.dropdown.sub.tsx";

vi.mock("@/components/shadcn/dropdown-menu", () => ({
  DropdownMenuItem: ({ children, asChild }: { children: React.ReactNode; asChild?: boolean }) =>
    asChild ? children : <div>{children}</div>,
}));

describe("<SidebarDropdownSub />", () => {
  const menu: MenuDef = {
    icon: UsersIcon,
    title: "menu.system.users",
    url: "/users",
    visible: always(),
  };

  it("should render dropdown link when mounted", () => {
    render(
      <MemoryRouter initialEntries={["/users"]}>
        <SidebarDropdownSub menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.getByRole("link", { name: /menu.system.users/i })).toHaveAttribute(
      "href",
      "/users",
    );
  });
});
