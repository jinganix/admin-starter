import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { RoleFilterForm } from "@/pages/role/role.filter.form.tsx";

const setQuery = vi.fn().mockResolvedValue(undefined);

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ query: {}, setQuery }),
}));

describe("<RoleFilterForm />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should submit filter values when search is clicked", async () => {
    render(<RoleFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("role.filter.nickname.placeholder"), "admin");
    await userEvent.click(screen.getByRole("button", { name: "action.search" }));

    await waitFor(() =>
      expect(setQuery).toHaveBeenCalledWith(expect.objectContaining({ name: "admin" })),
    );
  });

  it("should reset filters when reset is clicked", async () => {
    render(<RoleFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("role.filter.nickname.placeholder"), "admin");
    await userEvent.click(screen.getByRole("button", { name: "action.reset" }));

    await waitFor(() => expect(setQuery).toHaveBeenCalledWith({}));
  });

  it("should clear name when clear button is clicked", async () => {
    render(<RoleFilterForm />);

    const input = screen.getByPlaceholderText("role.filter.nickname.placeholder");
    await userEvent.type(input, "admin");
    await userEvent.click(screen.getAllByRole("button", { name: "" })[0]);

    expect(input).toHaveValue("");
  });
});
