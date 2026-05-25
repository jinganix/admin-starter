import { always } from "@helpers/condition/cond.utils.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { LogsIcon } from "lucide-react";
import { MemoryRouter } from "react-router";
import { afterEach, describe, expect, it, vi } from "vitest";
import { CollapsibleMenuSub } from "@/components/sidebar/collapsible.menu.sub.tsx";
import { MenuDef } from "@/components/sidebar/menus.tsx";

const setOpenMobile = vi.fn();
const satisfy = vi.fn();

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy }),
}));

vi.mock("@/components/shadcn/sidebar.tsx", () => ({
  SidebarMenuSubButton: ({
    children,
    asChild,
  }: {
    children: React.ReactNode;
    asChild?: boolean;
  }) => (asChild ? children : <button type="button">{children}</button>),
  SidebarMenuSubItem: ({ children }: { children: React.ReactNode }) => <li>{children}</li>,
  useSidebar: () => ({ setOpenMobile }),
}));

describe("<CollapsibleMenuSub />", () => {
  const menu: MenuDef = {
    icon: LogsIcon,
    title: "menu.system.audits",
    url: "/audits",
    visible: always(),
  };

  afterEach(() => vi.clearAllMocks());

  it("should render submenu link when visible cond is satisfied", () => {
    satisfy.mockReturnValue(true);

    render(
      <MemoryRouter initialEntries={["/audits"]}>
        <CollapsibleMenuSub menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.getByRole("link", { name: /menu.system.audits/i })).toHaveAttribute(
      "href",
      "/audits",
    );
  });

  it("should render nothing when visible cond is not satisfied", () => {
    satisfy.mockReturnValue(false);

    render(
      <MemoryRouter>
        <CollapsibleMenuSub menu={menu} />
      </MemoryRouter>,
    );

    expect(screen.queryByRole("link")).not.toBeInTheDocument();
  });

  it("should close mobile sidebar when submenu link is clicked", async () => {
    satisfy.mockReturnValue(true);

    render(
      <MemoryRouter initialEntries={["/audits"]}>
        <CollapsibleMenuSub menu={menu} />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("link", { name: /menu.system.audits/i }));

    expect(setOpenMobile).toHaveBeenCalledWith(false);
  });
});
