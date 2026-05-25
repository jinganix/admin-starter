import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import { PageLayout } from "@/components/layout/page.layout.tsx";

vi.mock("@/components/theme/theme.customizer.tsx", () => ({
  ThemeCustomizer: () => <div>theme-customizer</div>,
}));

vi.mock("@/components/layout/language.switch.tsx", () => ({
  LanguageSwitch: () => <div>language-switch</div>,
}));

vi.mock("@/components/ui/github.link.tsx", () => ({
  GithubLink: () => <div>github-link</div>,
}));

describe("<PageLayout />", () => {
  it("should render toolbar controls and outlet when mounted", () => {
    render(
      <MemoryRouter initialEntries={["/login"]}>
        <Routes>
          <Route element={<PageLayout />}>
            <Route path="/login" element={<span>login page</span>} />
          </Route>
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("theme-customizer")).toBeInTheDocument();
    expect(screen.getByText("language-switch")).toBeInTheDocument();
    expect(screen.getByText("github-link")).toBeInTheDocument();
    expect(screen.getByText("login page")).toBeInTheDocument();
  });
});
