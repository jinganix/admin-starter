import { DEFAULT_PAGE } from "@helpers/paging/pageable.ts";
import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { ReactNode } from "react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TablePageSize } from "@/components/table/table.page.size.tsx";

const setPageable = vi.fn();
let loading = false;

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({
    loading,
    pageable: { page: 2, size: 10, sort: {} },
    setPageable,
  }),
}));

vi.mock("@/components/shadcn/select.tsx", () => ({
  Select: ({
    children,
    disabled,
    onValueChange,
  }: {
    children: ReactNode;
    disabled?: boolean;
    onValueChange?: (value: string) => void;
  }) => (
    <div>
      {children}
      <button type="button" disabled={disabled} onClick={() => onValueChange?.("20")}>
        pick-size-20
      </button>
    </div>
  ),
  SelectContent: ({ children }: { children: ReactNode }) => <>{children}</>,
  SelectItem: () => null,
  SelectTrigger: ({ children }: { children: ReactNode }) => <>{children}</>,
  SelectValue: () => null,
}));

describe("<TablePageSize />", () => {
  afterEach(() => vi.resetAllMocks());

  it("should show rows per page label when mounted", () => {
    render(<TablePageSize />);

    expect(screen.getByText("table.footer.rowsPerPage")).toBeInTheDocument();
  });

  it("should reset page and update size when user selects new page size", async () => {
    render(<TablePageSize />);

    await userEvent.click(screen.getByRole("button", { name: "pick-size-20" }));

    expect(setPageable).toHaveBeenCalledWith({
      page: DEFAULT_PAGE,
      size: 20,
      sort: {},
    });
  });

  it("should disable page size select when table is loading", () => {
    loading = true;
    render(<TablePageSize />);

    expect(screen.getByRole("button", { name: "pick-size-20" })).toBeDisabled();
    loading = false;
  });
});
