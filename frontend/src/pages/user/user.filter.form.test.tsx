import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { UserFilterForm } from "@/pages/user/user.filter.form.tsx";

const setQuery = vi.fn().mockResolvedValue(undefined);

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ query: {}, setQuery }),
}));

describe("<UserFilterForm />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should submit filter values when search is clicked", async () => {
    render(<UserFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("audit.filter.userId"), "7");
    await userEvent.type(screen.getByPlaceholderText("user.filter.username.placeholder"), "bob");
    await userEvent.click(screen.getByRole("button", { name: "action.search" }));

    await waitFor(() =>
      expect(setQuery).toHaveBeenCalledWith(
        expect.objectContaining({ userId: "7", username: "bob" }),
      ),
    );
  });

  it("should reset filters when reset is clicked", async () => {
    render(<UserFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("user.filter.username.placeholder"), "bob");
    await userEvent.click(screen.getByRole("button", { name: "action.reset" }));

    await waitFor(() => expect(setQuery).toHaveBeenCalledWith({}));
  });

  it("should clear username when clear button is clicked", async () => {
    render(<UserFilterForm />);

    const input = screen.getByPlaceholderText("user.filter.username.placeholder");
    await userEvent.type(input, "bob");
    await userEvent.click(screen.getAllByRole("button", { name: "" })[1]);

    expect(input).toHaveValue("");
  });

  it("should clear user id when clear button is clicked", async () => {
    render(<UserFilterForm />);

    const input = screen.getByPlaceholderText("audit.filter.userId");
    await userEvent.type(input, "9");
    await userEvent.click(screen.getAllByRole("button", { name: "" })[0]);

    expect(input).toHaveValue(null);
  });
});
