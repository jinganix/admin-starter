import { act, render, RenderResult } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { DeleteDialogProps } from "@/components/dialog/delete.dialog.tsx";
import { DeleteButton } from "@/components/ui/delete.button.tsx";

let deleteDialogProps: DeleteDialogProps | null = null;
vi.mock("@/components/dialog/delete.dialog.tsx", () => ({
  DeleteDialog: (props: DeleteDialogProps) => {
    deleteDialogProps = props;
    return <div></div>;
  },
}));

describe("<DeleteButton />", () => {
  const onDelete = vi.fn();

  const setup = (): RenderResult => render(<DeleteButton onDelete={onDelete} />);

  beforeEach(() => (deleteDialogProps = null));

  afterEach(() => vi.resetAllMocks());

  describe("when rendered", () => {
    it("should match snapshot", () => {
      const { container } = setup();

      expect(container).toMatchSnapshot();
    });
  });

  describe("when clicked", () => {
    it("should open dialog", async () => {
      const element = setup();

      await act(() => userEvent.click(element.getByRole("button")));
      expect(deleteDialogProps?.open).toBeTruthy();
    });
  });

  describe("when dialog continue", () => {
    it("should call onDelete", async () => {
      const element = setup();

      await act(() => userEvent.click(element.getByRole("button")));
      await act(() => deleteDialogProps?.onContinue());
      expect(onDelete).toHaveBeenCalled();
    });

    describe("when onDelete return true", () => {
      it("should close dialog", async () => {
        onDelete.mockImplementationOnce(() => true);
        const element = setup();

        await act(() => userEvent.click(element.getByRole("button")));
        await act(() => deleteDialogProps?.onContinue());
        expect(deleteDialogProps?.open).toBeFalsy();
      });
    });

    describe("when onDelete return false", () => {
      it("should not close dialog", async () => {
        onDelete.mockImplementationOnce(() => false);
        const element = setup();

        await act(() => userEvent.click(element.getByRole("button")));
        await act(() => deleteDialogProps?.onContinue());
        expect(deleteDialogProps?.open).toBeTruthy();
      });
    });
  });

  describe("when dialog cancel", () => {
    it("should not call onDelete", async () => {
      const element = setup();

      await act(() => userEvent.click(element.getByRole("button")));
      await act(() => deleteDialogProps?.onCancel());
      expect(onDelete).not.toHaveBeenCalled();
      expect(deleteDialogProps?.open).toBeFalsy();
    });
  });
});
