import { UserStatus } from "@proto/SysUserProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { UserCreateDialog } from "@/pages/user/user.create.dialog.tsx";
import { RoleActions } from "@/sys/role/role.actions.ts";
import { UserActions } from "@/sys/user/user.actions.ts";

const loadData = vi.fn().mockResolvedValue(undefined);
const onOpenChange = vi.fn();

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ loadData }),
}));

vi.mock("@/sys/role/role.actions.ts", () => ({
  RoleActions: { getOptions: vi.fn() },
}));

vi.mock("@/sys/user/user.actions.ts", () => ({
  UserActions: { create: vi.fn() },
}));

vi.mock("@/components/ui/faceted.filter.tsx", () => ({
  FacetedFilter: ({ setSelected }: { setSelected: (values: string[]) => void }) => (
    <button type="button" onClick={() => setSelected(["r1"])}>
      pick roles
    </button>
  ),
}));

describe("<UserCreateDialog />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation errors when required fields are invalid", async () => {
    vi.mocked(RoleActions.getOptions).mockResolvedValue([]);
    render(<UserCreateDialog open onOpenChange={onOpenChange} />);

    await userEvent.click(screen.getByRole("button", { name: "user.dialog.create.submit" }));

    expect(await screen.findByText("auth.username.min")).toBeInTheDocument();
    expect(UserActions.create).not.toHaveBeenCalled();
  });

  it("should show password validation error when password is too short", async () => {
    vi.mocked(RoleActions.getOptions).mockResolvedValue([]);
    render(<UserCreateDialog open onOpenChange={onOpenChange} />);

    const inputs = screen.getAllByRole("textbox");
    await userEvent.type(inputs[0], "newuser");
    await userEvent.type(inputs[1], "12345");
    await userEvent.click(screen.getByRole("button", { name: "user.dialog.create.submit" }));

    expect(await screen.findByText("auth.password.min")).toBeInTheDocument();
  });

  it("should create user and close dialog when form is valid", async () => {
    vi.mocked(RoleActions.getOptions).mockResolvedValue([{ label: "Admin", value: "r1" }]);
    vi.mocked(UserActions.create).mockResolvedValue(true);

    render(<UserCreateDialog open onOpenChange={onOpenChange} />);

    await waitFor(() => expect(RoleActions.getOptions).toHaveBeenCalled());

    const inputs = screen.getAllByRole("textbox");
    await userEvent.type(inputs[0], "newuser");
    await userEvent.type(inputs[1], "secret12");
    await userEvent.click(screen.getByRole("button", { name: "pick roles" }));
    await userEvent.click(screen.getByRole("switch"));
    await userEvent.click(screen.getByRole("button", { name: "user.dialog.create.submit" }));

    await waitFor(() =>
      expect(UserActions.create).toHaveBeenCalledWith({
        password: "secret12",
        roleIds: ["r1"],
        status: UserStatus.INACTIVE,
        username: "newuser",
      }),
    );
    expect(loadData).toHaveBeenCalled();
    expect(onOpenChange).toHaveBeenCalledWith(false);
  });

  it("should reset form when dialog is closed", async () => {
    vi.mocked(RoleActions.getOptions).mockResolvedValue([]);
    render(<UserCreateDialog open onOpenChange={onOpenChange} />);

    const inputs = screen.getAllByRole("textbox");
    await userEvent.type(inputs[0], "newuser");
    await userEvent.keyboard("{Escape}");

    expect(onOpenChange).toHaveBeenCalledWith(false);
  });
});
