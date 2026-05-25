import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MonitorCogIcon, UsersIcon } from "lucide-react";
import { MemoryRouter } from "react-router";
import { describe, expect, it, vi } from "vitest";
import { CollapsibleMenu } from "@/components/sidebar/collapsible.menu.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

vi.mock("@/components/shadcn/collapsible.tsx", () => ({
  Collapsible: ({
    children,
    open,
    onOpenChange,
  }: {
    children: React.ReactNode;
    open?: boolean;
    onOpenChange?: (open: boolean) => void;
  }) => (
    <div data-open={open} data-testid="collapsible">
      <button type="button" onClick={() => onOpenChange?.(!open)}>
        toggle
      </button>
      {children}
    </div>
  ),
}));

vi.mock("@/components/sidebar/collapsible.menu.item.tsx", () => ({
  CollapsibleMenuItem: ({ menu }: { menu: MenuDef }) => <div>{menu.title}</div>,
}));

describe("<CollapsibleMenu />", () => {
  const menu: MenuDef = {
    icon: MonitorCogIcon,
    items: [{ icon: UsersIcon, title: "menu.system.users", url: "/users", visible: always() }],
    title: "menu.system.",
    url: "#",
  };

  it("should render collapsible menu when mounted", () => {
    render(
      <MemoryRouter initialEntries={["/users"]}>
        <CollapsibleMenu menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.getByText("menu.system.")).toBeInTheDocument();
  });

  it("should toggle open state when collapsible changes", async () => {
    render(
      <MemoryRouter initialEntries={["/"]}>
        <CollapsibleMenu menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.getByTestId("collapsible")).toHaveAttribute("data-open", "false");
    await userEvent.click(screen.getByRole("button", { name: "toggle" }));
    expect(screen.getByTestId("collapsible")).toHaveAttribute("data-open", "true");
  });
});
