import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AuditFilterForm } from "@/pages/audit/audit.filter.form.tsx";

const setQuery = vi.fn().mockResolvedValue(undefined);

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ query: {}, setQuery }),
}));

describe("<AuditFilterForm />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should submit filter values when search is clicked", async () => {
    render(<AuditFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("audit.filter.userId"), "42");
    await userEvent.type(screen.getByPlaceholderText("audit.filter.username"), "alice");
    await userEvent.click(screen.getByRole("button", { name: "action.search" }));

    await waitFor(() =>
      expect(setQuery).toHaveBeenCalledWith(
        expect.objectContaining({ userId: "42", username: "alice" }),
      ),
    );
  });

  it("should reset filters when reset is clicked", async () => {
    render(<AuditFilterForm />);

    await userEvent.type(screen.getByPlaceholderText("audit.filter.path"), "/api");
    await userEvent.click(screen.getByRole("button", { name: "action.reset" }));

    await waitFor(() => expect(setQuery).toHaveBeenCalledWith({}));
  });

  it("should clear user id when clear button is clicked", async () => {
    render(<AuditFilterForm />);

    const input = screen.getByPlaceholderText("audit.filter.userId");
    await userEvent.type(input, "99");
    const clearButtons = screen.getAllByRole("button", { name: "" });
    await userEvent.click(clearButtons[0]);

    expect(input).toHaveValue(null);
  });

  it.each([
    { field: "audit.filter.username", index: 1 },
    { field: "audit.filter.path", index: 2 },
    { field: "audit.filter.method", index: 3 },
  ])("should clear $field when clear button is clicked", async ({ field, index }) => {
    render(<AuditFilterForm />);

    const input = screen.getByPlaceholderText(field);
    await userEvent.type(input, "value");
    const clearButtons = screen.getAllByRole("button", { name: "" });
    await userEvent.click(clearButtons[index]);

    expect(input).toHaveValue("");
  });
});
