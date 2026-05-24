import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { ThemeCustomizer } from "@/components/theme/theme.customizer.tsx";

describe("<ThemeCustomizer />", () => {
  it("should render theme trigger button when mounted", () => {
    render(<ThemeCustomizer />);

    expect(screen.getByRole("button")).toBeInTheDocument();
  });

  it("should show theme palette when user opens menu", async () => {
    render(<ThemeCustomizer />);

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByText("theme.title.")).toBeInTheDocument();
  });
});
