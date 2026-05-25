import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TablePagination } from "@/components/table/table.pagination.tsx";

const setPageable = vi.fn();
let loading = false;
let pageable = { page: 0, size: 10, sort: {} };
let paging = { page: 0, pages: 5, size: 10, total: 50 };

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({
    loading,
    pageable,
    paging,
    setPageable,
  }),
}));

describe("<TablePagination />", () => {
  afterEach(() => {
    loading = false;
    pageable = { page: 0, size: 10, sort: {} };
    paging = { page: 0, pages: 5, size: 10, total: 50 };
    vi.resetAllMocks();
  });

  it("should disable first and previous controls on first page", () => {
    render(<TablePagination />);
    const buttons = screen.getAllByRole("button");

    expect(buttons[0]).toBeDisabled();
    expect(buttons[1]).toBeDisabled();
  });

  it("should disable next and last controls on last page", () => {
    pageable = { page: 4, size: 10, sort: {} };
    paging = { page: 4, pages: 5, size: 10, total: 50 };
    render(<TablePagination />);
    const buttons = screen.getAllByRole("button");

    expect(buttons[buttons.length - 2]).toBeDisabled();
    expect(buttons[buttons.length - 1]).toBeDisabled();
  });

  it("should navigate to selected page when user clicks page number", async () => {
    render(<TablePagination />);

    await userEvent.click(screen.getByRole("button", { name: "3" }));

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 2 }));
  });

  it("should go to first page when user clicks first control", async () => {
    pageable = { page: 2, size: 10, sort: {} };
    render(<TablePagination />);

    await userEvent.click(screen.getAllByRole("button")[0]);

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 0 }));
  });

  it("should go to previous page when user clicks previous control", async () => {
    pageable = { page: 2, size: 10, sort: {} };
    render(<TablePagination />);

    await userEvent.click(screen.getAllByRole("button")[1]);

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 1 }));
  });

  it("should go to next page when user clicks next control", async () => {
    pageable = { page: 1, size: 10, sort: {} };
    render(<TablePagination />);
    const buttons = screen.getAllByRole("button");

    await userEvent.click(buttons[buttons.length - 2]);

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 2 }));
  });

  it("should go to last page when user clicks last control", async () => {
    render(<TablePagination />);
    const buttons = screen.getAllByRole("button");

    await userEvent.click(buttons[buttons.length - 1]);

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 4 }));
  });

  it("should treat zero pages as single page for pagination data", () => {
    paging = { page: 0, pages: 0, size: 10, total: 0 };
    render(<TablePagination />);

    expect(screen.getByRole("button", { name: "1" })).toBeInTheDocument();
  });

  it("should disable all controls when table is loading", () => {
    loading = true;
    render(<TablePagination />);

    for (const button of screen.getAllByRole("button")) {
      expect(button).toBeDisabled();
    }
  });
});
