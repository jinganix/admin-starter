import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MonitorCogIcon, UsersIcon } from "lucide-react";
import { MemoryRouter } from "react-router";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { SidebarProvider } from "@/components/shadcn/sidebar";
import { MenuDef } from "@/components/sidebar/menus.tsx";
import { SidebarDropdownMenu } from "@/components/sidebar/sidebar.dropdown.menu.tsx";

describe("<SidebarDropdownMenu />", () => {
  const menu: MenuDef = {
    icon: MonitorCogIcon,
    items: [{ icon: UsersIcon, title: "menu.system.users", url: "/users", visible: always() }],
    title: "menu.system.",
    url: "#",
  };

  beforeEach(() => {
    Object.defineProperty(window, "matchMedia", {
      value: vi.fn().mockImplementation((query: string) => ({
        addEventListener: vi.fn(),
        addListener: vi.fn(),
        dispatchEvent: vi.fn(),
        matches: false,
        media: query,
        onchange: null,
        removeEventListener: vi.fn(),
        removeListener: vi.fn(),
      })),
      writable: true,
    });
  });

  it("should render dropdown trigger when mounted", () => {
    render(
      <MemoryRouter>
        <SidebarProvider>
          <SidebarDropdownMenu menu={menu} />
        </SidebarProvider>
      </MemoryRouter>,
    );

    expect(screen.getByRole("button", { name: /menu.system./i })).toBeInTheDocument();
  });

  it("should show submenu items when dropdown is opened", async () => {
    render(
      <MemoryRouter>
        <SidebarProvider>
          <SidebarDropdownMenu menu={menu} />
        </SidebarProvider>
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button", { name: /menu.system./i }));

    expect(await screen.findByText("menu.system.users")).toBeInTheDocument();
  });
});
