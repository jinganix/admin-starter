import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { ConfirmActionDialog } from "@/components/dialog/confirm.action.dialog.tsx";

describe("<ConfirmActionDialog />", () => {
  it("should call onCancel when cancel is clicked", async () => {
    const onCancel = vi.fn();
    render(
      <ConfirmActionDialog open onCancel={onCancel} onContinue={vi.fn().mockResolvedValue(true)} />,
    );

    await userEvent.click(screen.getByText("action.cancel"));
    expect(onCancel).toHaveBeenCalledOnce();
  });

  it("should call onContinue when continue is clicked", async () => {
    const onContinue = vi.fn().mockResolvedValue(true);
    render(<ConfirmActionDialog open onCancel={vi.fn()} onContinue={onContinue} />);

    await userEvent.click(screen.getByText("action.continue"));
    expect(onContinue).toHaveBeenCalledOnce();
  });
});
