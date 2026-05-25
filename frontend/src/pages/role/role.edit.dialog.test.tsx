import { RoleStatus } from "@proto/SysRoleProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { RoleEditDialog } from "@/pages/role/role.edit.dialog.tsx";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";
import { RoleActions } from "@/sys/role/role.actions.ts";

const loadData = vi.fn().mockResolvedValue(undefined);
const setRecords = vi.fn();
const onOpenChange = vi.fn();

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ loadData, records: [], setRecords }),
}));

vi.mock("@/sys/permission/permission.actions.ts", () => ({
  PermissionActions: { getOptions: vi.fn() },
}));

vi.mock("@/sys/role/role.actions.ts", () => ({
  RoleActions: { create: vi.fn(), update: vi.fn() },
}));

vi.mock("@/components/tree/tree.view.tsx", () => ({
  TreeView: ({ setSelected }: { setSelected: (values: string[]) => void }) => (
    <button type="button" onClick={() => setSelected(["perm-1"])}>
      pick permission
    </button>
  ),
}));

describe("<RoleEditDialog />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation errors when creating with invalid fields", async () => {
    vi.mocked(PermissionActions.getOptions).mockResolvedValue([]);
    render(<RoleEditDialog open onOpenChange={onOpenChange} />);

    await userEvent.click(screen.getByRole("button", { name: "role.dialog.submit" }));

    expect(await screen.findByText("role.edit.code.min")).toBeInTheDocument();
    expect(RoleActions.create).not.toHaveBeenCalled();
  });

  it("should create role and reload when form is valid", async () => {
    vi.mocked(PermissionActions.getOptions).mockResolvedValue([
      { code: "USER", label: "user.group", value: "g1" },
      { code: "USER.READ", label: "user.read", value: "p1" },
    ]);
    vi.mocked(RoleActions.create).mockResolvedValue(true);

    render(<RoleEditDialog open onOpenChange={onOpenChange} />);

    await waitFor(() => expect(PermissionActions.getOptions).toHaveBeenCalled());

    const inputs = screen.getAllByRole("textbox");
    await userEvent.type(inputs[0], "ROLE_CODE");
    await userEvent.type(inputs[1], "Role description");
    await userEvent.type(inputs[2], "Admin Role");
    await userEvent.click(screen.getByRole("button", { name: "pick permission" }));
    await userEvent.click(screen.getByRole("switch"));
    await userEvent.click(screen.getByRole("button", { name: "role.dialog.submit" }));

    await waitFor(() =>
      expect(RoleActions.create).toHaveBeenCalledWith(
        expect.objectContaining({
          code: "ROLE_CODE",
          name: "Admin Role",
          permissionIds: ["perm-1"],
          status: RoleStatus.INACTIVE,
        }),
      ),
    );
    expect(loadData).toHaveBeenCalled();
    expect(onOpenChange).toHaveBeenCalledWith(false);
  });

  it("should update role when editing existing role", async () => {
    const role = {
      code: "ADMIN",
      createdAt: 1,
      description: "role.admin",
      id: "r1",
      name: "Admin",
      permissionIds: [] as string[],
      status: RoleStatus.ACTIVE,
    };
    vi.mocked(PermissionActions.getOptions).mockResolvedValue([]);
    vi.mocked(RoleActions.update).mockResolvedValue({ ...role, name: "Updated" });

    render(<RoleEditDialog role={role} open onOpenChange={onOpenChange} />);

    const nameInput = screen.getAllByRole("textbox")[2];
    await userEvent.clear(nameInput);
    await userEvent.type(nameInput, "Updated");
    await userEvent.click(screen.getByRole("button", { name: "role.dialog.submit" }));

    await waitFor(() =>
      expect(RoleActions.update).toHaveBeenCalledWith(
        "r1",
        expect.objectContaining({ name: "Updated", permissionIds: [] }),
      ),
    );
    expect(setRecords).toHaveBeenCalled();
  });
});
