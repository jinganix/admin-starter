import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { Spinner } from "@/components/ui/spinner.tsx";

describe("<Spinner />", () => {
  it("should show loading overlay when loading is true", () => {
    const { container } = render(<Spinner loading />);

    expect(container.querySelector(".animate-spin")).toBeInTheDocument();
  });

  it("should render empty when loading is false", () => {
    const { container } = render(<Spinner loading={false} />);

    expect(container).toBeEmptyDOMElement();
    expect(screen.queryByRole("img")).not.toBeInTheDocument();
  });
});
