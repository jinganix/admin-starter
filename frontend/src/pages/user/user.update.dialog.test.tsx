import { UserStatus } from "@proto/SysUserProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { UserUpdateDialog } from "@/pages/user/user.update.dialog.tsx";
import { RoleActions } from "@/sys/role/role.actions.ts";
import { UserActions } from "@/sys/user/user.actions.ts";

const setRecords = vi.fn();
const onOpenChange = vi.fn();
const user = {
  createdAt: 1,
  id: "u1",
  nickname: "Alice",
  roleIds: ["r1"],
  status: UserStatus.ACTIVE,
  username: "alice",
};

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ records: [user], setRecords }),
}));

vi.mock("@/sys/role/role.actions.ts", () => ({
  RoleActions: { getOptions: vi.fn() },
}));

vi.mock("@/sys/user/user.actions.ts", () => ({
  UserActions: { retrieve: vi.fn(), update: vi.fn() },
}));

vi.mock("@/components/ui/faceted.filter.tsx", () => ({
  FacetedFilter: () => null,
}));

describe("<UserUpdateDialog />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should show validation error when nickname is too short", async () => {
    vi.mocked(UserActions.retrieve).mockResolvedValue(user);
    vi.mocked(RoleActions.getOptions).mockResolvedValue([]);

    render(<UserUpdateDialog userId="u1" open onOpenChange={onOpenChange} />);

    await waitFor(() => expect(screen.queryByRole("progressbar")).not.toBeInTheDocument());

    const input = screen.getByRole("textbox");
    await userEvent.clear(input);
    await userEvent.type(input, "ab");
    await userEvent.click(screen.getByRole("button", { name: "user.dialog.update.submit" }));

    expect(await screen.findByText("settings.profile.nickname.min")).toBeInTheDocument();
    expect(UserActions.update).not.toHaveBeenCalled();
  });

  it("should update user and close dialog when form is valid", async () => {
    vi.mocked(UserActions.retrieve).mockResolvedValue(user);
    vi.mocked(RoleActions.getOptions).mockResolvedValue([]);
    vi.mocked(UserActions.update).mockResolvedValue({ ...user, nickname: "Updated" });

    render(<UserUpdateDialog userId="u1" open onOpenChange={onOpenChange} />);

    await waitFor(() => expect(screen.getByDisplayValue("Alice")).toBeInTheDocument());

    const input = screen.getByRole("textbox");
    await userEvent.clear(input);
    await userEvent.type(input, "Updated");
    await userEvent.click(screen.getByRole("button", { name: "user.dialog.update.submit" }));

    await waitFor(() =>
      expect(UserActions.update).toHaveBeenCalledWith("u1", {
        nickname: "Updated",
        roleIds: ["r1"],
        status: UserStatus.ACTIVE,
      }),
    );
    expect(setRecords).toHaveBeenCalled();
    expect(onOpenChange).toHaveBeenCalledWith(false);
  });
});
