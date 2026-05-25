import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { Header } from "@/components/layout/header.tsx";

const useWindowScroll = vi.fn();

vi.mock("react-use", () => ({
  useWindowScroll: () => useWindowScroll(),
}));

vi.mock("@/components/shadcn/sidebar.tsx", () => ({
  SidebarTrigger: () => <button type="button">sidebar-trigger</button>,
}));

describe("<Header />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render sidebar trigger and children when mounted", () => {
    useWindowScroll.mockReturnValue({ y: 0 });

    render(
      <Header>
        <span>header content</span>
      </Header>,
    );

    expect(screen.getByText("sidebar-trigger")).toBeInTheDocument();
    expect(screen.getByText("header content")).toBeInTheDocument();
  });

  it("should apply drop shadow when fixed header is scrolled", () => {
    useWindowScroll.mockReturnValue({ y: 20 });

    render(<Header fixed>content</Header>);

    expect(screen.getByRole("banner")).toHaveClass("drop-shadow-md");
  });

  it("should not apply drop shadow when scroll offset is small", () => {
    useWindowScroll.mockReturnValue({ y: 5 });

    render(<Header fixed>content</Header>);

    expect(screen.getByRole("banner")).toHaveClass("shadow-none");
  });
});
