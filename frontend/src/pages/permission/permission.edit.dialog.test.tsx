import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { PermissionEditDialog } from "@/pages/permission/permission.edit.dialog.tsx";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";

const loadData = vi.fn().mockResolvedValue(undefined);
const setRecords = vi.fn();
const onOpenChange = vi.fn();

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ loadData, records: [], setRecords }),
}));

vi.mock("@/sys/permission/permission.actions.ts", () => ({
  PermissionActions: { create: vi.fn(), update: vi.fn() },
}));

describe("<PermissionEditDialog />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation errors when creating with invalid fields", async () => {
    render(<PermissionEditDialog open onOpenChange={onOpenChange} />);

    await userEvent.click(screen.getByRole("button", { name: "permission.dialog.submit" }));

    expect(await screen.findByText("role.edit.code.min")).toBeInTheDocument();
    expect(PermissionActions.create).not.toHaveBeenCalled();
  });

  it("should create permission and reload when form is valid", async () => {
    vi.mocked(PermissionActions.create).mockResolvedValue(true);

    render(<PermissionEditDialog open onOpenChange={onOpenChange} />);

    const inputs = screen.getAllByRole("textbox");
    await userEvent.type(inputs[0], "perm.name");
    await userEvent.type(inputs[1], "PERM_CODE");
    await userEvent.click(screen.getByRole("button", { name: "permission.dialog.submit" }));

    await waitFor(() =>
      expect(PermissionActions.create).toHaveBeenCalledWith(
        expect.objectContaining({
          code: "PERM_CODE",
          name: "perm.name",
          status: PermissionStatus.ACTIVE,
          type: PermissionType.API,
        }),
      ),
    );
    expect(loadData).toHaveBeenCalled();
    expect(onOpenChange).toHaveBeenCalledWith(false);
  });

  it("should toggle status switch when editing permission", async () => {
    const permission = {
      code: "READ",
      createdAt: 1,
      description: "read",
      id: "p1",
      name: "permission.read",
      status: PermissionStatus.ACTIVE,
      type: PermissionType.API,
    };

    render(<PermissionEditDialog permission={permission} open onOpenChange={onOpenChange} />);

    await userEvent.click(screen.getByRole("switch"));
    await userEvent.click(screen.getByRole("button", { name: "permission.dialog.submit" }));

    await waitFor(() =>
      expect(PermissionActions.update).toHaveBeenCalledWith(
        "p1",
        expect.objectContaining({ status: PermissionStatus.INACTIVE }),
      ),
    );
  });

  it("should update permission when editing existing permission", async () => {
    const permission = {
      code: "READ",
      createdAt: 1,
      description: "read",
      id: "p1",
      name: "permission.read",
      status: PermissionStatus.ACTIVE,
      type: PermissionType.API,
    };
    vi.mocked(PermissionActions.update).mockResolvedValue({
      ...permission,
      name: "permission.updated",
    });

    render(<PermissionEditDialog permission={permission} open onOpenChange={onOpenChange} />);

    const nameInput = screen.getAllByRole("textbox")[0];
    await userEvent.clear(nameInput);
    await userEvent.type(nameInput, "permission.updated");
    await userEvent.click(screen.getByRole("button", { name: "permission.dialog.submit" }));

    await waitFor(() =>
      expect(PermissionActions.update).toHaveBeenCalledWith(
        "p1",
        expect.objectContaining({ name: "permission.updated" }),
      ),
    );
    expect(setRecords).toHaveBeenCalled();
  });
});
