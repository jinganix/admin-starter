import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { PermissionFilterForm } from "@/pages/permission/permission.filter.form.tsx";

const setQuery = vi.fn().mockResolvedValue(undefined);

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ query: {}, setQuery }),
}));

vi.mock("@/components/ui/faceted.filter.tsx", () => ({
  FacetedFilter: ({ setSelected }: { setSelected: (values: number[]) => void }) => (
    <button type="button" onClick={() => setSelected([1])}>
      pick type
    </button>
  ),
}));

describe("<PermissionFilterForm />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should submit filter values when search is clicked", async () => {
    render(<PermissionFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("permission.filter.code.placeholder"), "USER");
    await userEvent.click(screen.getByRole("button", { name: "action.search" }));

    await waitFor(() =>
      expect(setQuery).toHaveBeenCalledWith(expect.objectContaining({ code: "USER" })),
    );
  });

  it("should reset filters when reset is clicked", async () => {
    render(<PermissionFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("permission.filter.code.placeholder"), "USER");
    await userEvent.click(screen.getByRole("button", { name: "action.reset" }));

    await waitFor(() => expect(setQuery).toHaveBeenCalledWith({}));
  });

  it("should set types when type filter is picked", async () => {
    render(<PermissionFilterForm />);

    await userEvent.click(screen.getByRole("button", { name: "pick type" }));
    await userEvent.click(screen.getByRole("button", { name: "action.search" }));

    await waitFor(() =>
      expect(setQuery).toHaveBeenCalledWith(expect.objectContaining({ types: [1] })),
    );
  });

  it("should clear code when clear button is clicked", async () => {
    render(<PermissionFilterForm />);

    const input = screen.getByPlaceholderText("permission.filter.code.placeholder");
    await userEvent.type(input, "USER");
    await userEvent.click(screen.getAllByRole("button", { name: "" })[0]);

    expect(input).toHaveValue("");
  });
});
