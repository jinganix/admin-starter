import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { LayoutContent } from "@/components/layout/layout.content.tsx";

describe("<LayoutContent />", () => {
  it("should render main content when mounted", () => {
    render(<LayoutContent>page body</LayoutContent>);

    expect(screen.getByRole("main")).toHaveTextContent("page body");
  });

  it("should apply fixed layout class when fixed is true", () => {
    render(<LayoutContent fixed>fixed body</LayoutContent>);

    expect(screen.getByRole("main")).toHaveClass("fixed-main");
  });
});
