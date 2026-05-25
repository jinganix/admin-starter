import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { TableFooter } from "@/components/table/table.footer.tsx";

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({
    loading: false,
    pageable: { page: 1, size: 10, sort: {} },
    paging: { page: 1, pages: 5, size: 10, total: 42 },
    setPageable: vi.fn(),
  }),
}));

describe("<TableFooter />", () => {
  it("should show total records when mounted", () => {
    render(<TableFooter />);

    expect(screen.getByText("table.footer.totalRecords: 42")).toBeInTheDocument();
  });

  it("should show mobile page summary when mounted", () => {
    render(<TableFooter />);

    expect(screen.getByText("table.footer.page")).toBeInTheDocument();
  });

  it("should render pagination controls when mounted", () => {
    render(<TableFooter />);

    expect(screen.getByRole("spinbutton")).toBeInTheDocument();
    expect(screen.getByRole("combobox")).toBeInTheDocument();
    expect(screen.getAllByRole("button").length).toBeGreaterThan(1);
  });

  it("should apply custom className when provided", () => {
    const { container } = render(<TableFooter className="custom-footer" />);

    expect(container.firstChild).toHaveClass("custom-footer");
  });
});
