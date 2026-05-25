import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { TableRowActions } from "@/components/table/table.row.actions.tsx";

vi.mock("i18next", () => ({
  default: {
    t: (key: string) => key,
  },
}));

describe("<TableRowActions />", () => {
  const setAction = vi.fn();
  const item = { id: "1" };

  afterEach(() => vi.clearAllMocks());

  it("should render row action trigger when mounted", () => {
    render(<TableRowActions item={item} setAction={setAction} />);

    expect(screen.getByRole("button")).toBeInTheDocument();
  });

  it("should call setAction with edit when user selects edit", async () => {
    render(<TableRowActions item={item} setAction={setAction} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("action.edit"));

    expect(setAction).toHaveBeenCalledWith({ item, type: "edit" });
  });

  it("should call setAction with delete when user selects delete", async () => {
    render(<TableRowActions item={item} setAction={setAction} />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("action.delete"));

    expect(setAction).toHaveBeenCalledWith({ item, type: "delete" });
  });

  it("should render custom children when provided", async () => {
    render(
      <TableRowActions item={item} setAction={setAction}>
        <div>custom-action</div>
      </TableRowActions>,
    );

    await userEvent.click(screen.getByRole("button"));

    expect(screen.getByText("custom-action")).toBeInTheDocument();
  });
});
