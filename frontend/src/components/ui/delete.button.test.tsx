import { act, render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ConfirmDialogProps } from "@/components/dialog/confirm.action.dialog.tsx";
import { DeleteButton } from "@/components/ui/delete.button.tsx";

let deleteDialogProps: ConfirmDialogProps | null = null;
vi.mock("@/components/dialog/confirm.action.dialog.tsx", () => ({
  ConfirmActionDialog: (props: ConfirmDialogProps) => {
    deleteDialogProps = props;
    return <div></div>;
  },
}));

describe("<DeleteButton />", () => {
  const onDelete = vi.fn();

  beforeEach(() => {
    deleteDialogProps = null;
    onDelete.mockResolvedValue(true);
  });

  afterEach(() => vi.resetAllMocks());

  it("should render delete button when mounted", () => {
    render(<DeleteButton onDelete={onDelete} />);

    expect(screen.getByRole("button")).toBeInTheDocument();
  });

  it("should open dialog when user clicks delete", async () => {
    render(<DeleteButton onDelete={onDelete} />);

    await userEvent.click(screen.getByRole("button"));

    expect(deleteDialogProps?.open).toBeTruthy();
  });

  it("should call onDelete when user confirms dialog", async () => {
    render(<DeleteButton onDelete={onDelete} />);

    await userEvent.click(screen.getByRole("button"));
    await deleteDialogProps?.onContinue();

    expect(onDelete).toHaveBeenCalledOnce();
  });

  it("should close dialog when onDelete succeeds", async () => {
    onDelete.mockResolvedValueOnce(true);
    render(<DeleteButton onDelete={onDelete} />);

    await userEvent.click(screen.getByRole("button"));
    await act(async () => {
      await deleteDialogProps?.onContinue();
    });

    expect(deleteDialogProps?.open).toBeFalsy();
  });

  it("should keep dialog open when onDelete fails", async () => {
    onDelete.mockResolvedValueOnce(false);
    render(<DeleteButton onDelete={onDelete} />);

    await userEvent.click(screen.getByRole("button"));
    await deleteDialogProps?.onContinue();

    expect(deleteDialogProps?.open).toBeTruthy();
  });

  it("should close dialog without calling onDelete when user cancels", async () => {
    render(<DeleteButton onDelete={onDelete} />);

    await userEvent.click(screen.getByRole("button"));
    await act(() => deleteDialogProps?.onCancel());

    expect(onDelete).not.toHaveBeenCalled();
    expect(deleteDialogProps?.open).toBeFalsy();
  });
});
