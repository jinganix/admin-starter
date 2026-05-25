import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { GithubLink } from "@/components/ui/github.link.tsx";

describe("<GithubLink />", () => {
  it("should render github link when mounted", () => {
    render(<GithubLink />);

    const link = screen.getByRole("link", { name: "GitHub" });
    expect(link).toHaveAttribute("href", "https://github.com/jinganix/admin-starter");
    expect(link).toHaveAttribute("target", "_blank");
  });
});
