import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TablePageGoto } from "@/components/table/table.page.goto.tsx";

const setPageable = vi.fn();

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({
    loading: false,
    pageable: { page: 0, size: 10, sort: {} },
    paging: { page: 0, pages: 5, size: 10, total: 50 },
    setPageable,
  }),
}));

describe("<TablePageGoto />", () => {
  it("should clamp page index when user submits goto", async () => {
    render(<TablePageGoto />);
    const input = screen.getByRole("spinbutton");

    await userEvent.type(input, "99");
    await userEvent.click(screen.getAllByRole("button")[0]);

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 4 }));
  });

  it("should navigate on enter key", async () => {
    render(<TablePageGoto />);
    const input = screen.getByRole("spinbutton");

    await userEvent.type(input, "2{Enter}");

    expect(setPageable).toHaveBeenCalledWith(expect.objectContaining({ page: 1 }));
  });

  it("should not call setPageable when goto is empty", async () => {
    setPageable.mockClear();
    render(<TablePageGoto />);

    await userEvent.click(screen.getAllByRole("button")[0]);

    expect(setPageable).not.toHaveBeenCalled();
  });
});
