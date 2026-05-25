import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import { describe, expect, it, vi } from "vitest";
import { AuthedPageLayout } from "@/components/layout/authed.page.layout.tsx";

vi.mock("@/components/shadcn/sidebar", () => ({
  SidebarProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

vi.mock("@/components/sidebar/app.sidebar", () => ({
  AppSidebar: () => <aside>app-sidebar</aside>,
}));

vi.mock("@/components/layout/header.tsx", () => ({
  Header: ({ children }: { children: React.ReactNode }) => <header>{children}</header>,
}));

vi.mock("@/components/theme/theme.customizer.tsx", () => ({
  ThemeCustomizer: () => <div>theme-customizer</div>,
}));

vi.mock("@/components/layout/language.switch.tsx", () => ({
  LanguageSwitch: () => <div>language-switch</div>,
}));

vi.mock("@/components/ui/github.link.tsx", () => ({
  GithubLink: () => <div>github-link</div>,
}));

vi.mock("@/components/layout/user.nav.tsx", () => ({
  UserNav: () => <div>user-nav</div>,
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    Outlet: () => <main>outlet-content</main>,
  };
});

describe("<AuthedPageLayout />", () => {
  it("should render sidebar header controls and outlet when mounted", () => {
    render(
      <MemoryRouter>
        <AuthedPageLayout />
      </MemoryRouter>,
    );

    expect(screen.getByText("app-sidebar")).toBeInTheDocument();
    expect(screen.getByText("theme-customizer")).toBeInTheDocument();
    expect(screen.getByText("language-switch")).toBeInTheDocument();
    expect(screen.getByText("github-link")).toBeInTheDocument();
    expect(screen.getByText("user-nav")).toBeInTheDocument();
    expect(screen.getByText("outlet-content")).toBeInTheDocument();
    expect(document.getElementById("content")).toBeInTheDocument();
  });
});
