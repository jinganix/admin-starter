import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { PermissionTableActions } from "@/pages/permission/permission.table.actions.tsx";
import { AuthorityActions } from "@/sys/authority/authority.actions.ts";

const loadData = vi.fn().mockResolvedValue(undefined);

vi.mock("@/components/table/table.data.context.tsx", () => ({
  useTableData: () => ({ loadData }),
}));

vi.mock("@/sys/authority/authority.actions.ts", () => ({
  AuthorityActions: {
    reloadAPI: vi.fn(),
    uploadUI: vi.fn(),
  },
}));

describe("<PermissionTableActions />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should reload table data when ui sync succeeds", async () => {
    vi.mocked(AuthorityActions.uploadUI).mockResolvedValue(true);

    render(<PermissionTableActions />);
    await userEvent.click(screen.getByRole("button", { name: /permission.action.syncUI/i }));

    await waitFor(() => expect(loadData).toHaveBeenCalled());
  });

  it("should not reload table data when ui sync fails", async () => {
    vi.mocked(AuthorityActions.uploadUI).mockResolvedValue(false);

    render(<PermissionTableActions />);
    await userEvent.click(screen.getByRole("button", { name: /permission.action.syncUI/i }));

    await waitFor(() => expect(AuthorityActions.uploadUI).toHaveBeenCalled());
    expect(loadData).not.toHaveBeenCalled();
  });

  it("should reload table data when api reload succeeds", async () => {
    vi.mocked(AuthorityActions.reloadAPI).mockResolvedValue(true);

    render(<PermissionTableActions />);
    await userEvent.click(screen.getByRole("button", { name: /permission.action.reloadApi/i }));

    await waitFor(() => expect(loadData).toHaveBeenCalled());
  });
});
