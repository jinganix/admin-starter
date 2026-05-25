import { DEFAULT_PAGEABLE, DEFAULT_PAGING } from "@helpers/paging/pageable.ts";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { PermissionsComponent, PermissionsPage } from "@/pages/permissions.page.tsx";
import { PermissionActions } from "@/sys/permission/permission.actions.ts";

const tableMocks = vi.hoisted(() => {
  const mockPermission = {
    code: "USER_READ",
    createdAt: 1704067200000,
    description: "read users",
    id: "p1",
    name: "permission.user.read",
    status: 1 as PermissionStatus,
    type: 1 as PermissionType,
  };
  return {
    loadData: vi.fn().mockResolvedValue(undefined),
    mockPermission,
    query: {} as { types?: PermissionType[] },
    records: [mockPermission],
    setQuery: vi.fn(),
    setRecords: vi.fn(),
  };
});

vi.mock("@/components/table/table.data.context.tsx", async (importActual) => {
  const actual = await importActual<typeof import("@/components/table/table.data.context.tsx")>();
  return {
    ...actual,
    useTableData: () => ({
      loadData: tableMocks.loadData,
      loading: false,
      pageable: DEFAULT_PAGEABLE,
      paging: DEFAULT_PAGING,
      query: tableMocks.query,
      records: tableMocks.records,
      setPageable: vi.fn(),
      setPaging: vi.fn(),
      setQuery: tableMocks.setQuery,
      setRecords: tableMocks.setRecords,
    }),
  };
});

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy: () => true }),
}));

vi.mock("@/sys/permission/permission.actions.ts", () => ({
  PermissionActions: {
    delete: vi.fn(),
    list: vi.fn(),
    updateStatus: vi.fn(),
  },
}));

vi.mock("@/pages/permission/permission.table.actions.tsx", () => ({
  PermissionTableActions: () => <div>table actions</div>,
}));
vi.mock("@/pages/permission/permission.edit.dialog.tsx", () => ({
  PermissionEditDialog: ({ open }: { open: boolean }) =>
    open ? <div>permission dialog</div> : null,
}));

describe("<PermissionsComponent />", () => {
  beforeEach(() => {
    tableMocks.records = [tableMocks.mockPermission];
    tableMocks.query = {};
    vi.clearAllMocks();
  });

  afterEach(() => vi.resetAllMocks());

  it("should render permission table rows", () => {
    render(<PermissionsComponent />);

    expect(screen.getByText("permission.title.")).toBeInTheDocument();
    expect(screen.getByText("permission.user.read")).toBeInTheDocument();
    expect(screen.getByText("USER_READ")).toBeInTheDocument();
    expect(screen.getByText("table actions")).toBeInTheDocument();
  });

  it("should filter by type when type cell is clicked", async () => {
    render(<PermissionsComponent />);

    await userEvent.click(screen.getByText("permission.type.API"));

    expect(tableMocks.setQuery).toHaveBeenCalledWith({ types: [PermissionType.API] });
  });

  it("should open create dialog when add is clicked", async () => {
    render(<PermissionsComponent />);

    await userEvent.click(screen.getByRole("button", { name: /action.add/i }));

    expect(screen.getByText("permission dialog")).toBeInTheDocument();
  });

  it("should update status when status switch is toggled", async () => {
    vi.mocked(PermissionActions.updateStatus).mockResolvedValue(true);

    render(<PermissionsComponent />);

    await userEvent.click(screen.getByRole("switch"));

    await waitFor(() =>
      expect(PermissionActions.updateStatus).toHaveBeenCalledWith("p1", PermissionStatus.INACTIVE),
    );
    expect(tableMocks.setRecords).toHaveBeenCalled();
  });

  it("should delete selected permissions when bulk delete succeeds", async () => {
    vi.mocked(PermissionActions.delete).mockResolvedValue(true);

    render(<PermissionsComponent />);

    await userEvent.click(screen.getByLabelText("Select row"));
    await userEvent.click(screen.getByRole("button", { name: "action.delete" }));
    await userEvent.click(screen.getByRole("button", { name: "action.continue" }));

    await waitFor(() => expect(PermissionActions.delete).toHaveBeenCalledWith(["p1"]));
    expect(tableMocks.loadData).toHaveBeenCalled();
  });
});

let mockParams = new URLSearchParams();

vi.mock("react-router", () => ({
  useSearchParams: () => [mockParams, vi.fn()],
}));

describe("<PermissionsPage />", () => {
  it("should load permissions when page mounts", async () => {
    mockParams = new URLSearchParams();
    vi.mocked(PermissionActions.list).mockResolvedValue({
      paging: DEFAULT_PAGING,
      records: [tableMocks.mockPermission],
    });

    render(<PermissionsPage />);

    await waitFor(() => expect(PermissionActions.list).toHaveBeenCalled());
  });
});
