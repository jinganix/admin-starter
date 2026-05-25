import { render, screen } from "@testing-library/react";
import { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { SettingsLayout } from "@/pages/settings/settings.layout.tsx";

vi.mock("@/components/layout/layout.content.tsx", () => ({
  LayoutContent: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}));

vi.mock("@/components/sidebar/sidebar.nav.tsx", () => ({
  SidebarNav: ({ items }: { items: { title: string }[] }) => (
    <nav>
      {items.map((item) => (
        <span key={item.title}>{item.title}</span>
      ))}
    </nav>
  ),
}));

vi.mock("react-router-dom", () => ({
  Outlet: () => <span>profile pane</span>,
}));

describe("<SettingsLayout />", () => {
  it("should render settings navigation and outlet content", () => {
    render(<SettingsLayout />);

    expect(screen.getByRole("heading", { level: 1, name: "settings.title" })).toBeInTheDocument();
    expect(screen.getByText("settings.description")).toBeInTheDocument();
    expect(screen.getByText("settings.profile.")).toBeInTheDocument();
    expect(screen.getByText("settings.credential.")).toBeInTheDocument();
    expect(screen.getByText("profile pane")).toBeInTheDocument();
  });
});
