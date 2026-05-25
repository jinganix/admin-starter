import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { afterEach, describe, expect, it, vi } from "vitest";
import { SidebarNav } from "@/components/sidebar/sidebar.nav.tsx";

const navigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigate,
  };
});

vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router");
  return {
    ...actual,
    Link: ({
      to,
      children,
      className,
    }: {
      to: string;
      children: React.ReactNode;
      className?: string;
    }) => (
      <a href={to} className={className}>
        {children}
      </a>
    ),
  };
});

vi.mock("@/components/shadcn/select.tsx", () => ({
  Select: ({
    children,
    onValueChange,
    value,
  }: {
    children: React.ReactNode;
    onValueChange: (value: string) => void;
    value: string;
  }) => (
    <div>
      <button
        type="button"
        aria-label="mobile-nav-trigger"
        onClick={() => onValueChange("/settings/credential")}
      >
        {value}
      </button>
      {children}
    </div>
  ),
  SelectContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectItem: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SelectValue: () => null,
}));

describe("<SidebarNav />", () => {
  const items = [
    { href: "/settings/profile", icon: <span>profile-icon</span>, title: "Profile" },
    { href: "/settings/credential", icon: <span>credential-icon</span>, title: "Credential" },
  ];

  afterEach(() => vi.clearAllMocks());

  it("should render desktop nav links when mounted", () => {
    render(
      <MemoryRouter initialEntries={["/settings/profile"]}>
        <SidebarNav items={items} />
      </MemoryRouter>,
    );

    expect(screen.getByRole("link", { name: /Profile/i })).toHaveAttribute(
      "href",
      "/settings/profile",
    );
    expect(screen.getByRole("link", { name: /Credential/i })).toHaveAttribute(
      "href",
      "/settings/credential",
    );
  });

  it("should navigate when mobile select value changes", async () => {
    render(
      <MemoryRouter initialEntries={["/settings/profile"]}>
        <SidebarNav items={items} />
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole("button", { name: "mobile-nav-trigger" }));

    expect(navigate).toHaveBeenCalledWith("/settings/credential");
  });
});
