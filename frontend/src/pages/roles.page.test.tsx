import { DEFAULT_PAGEABLE, DEFAULT_PAGING } from "@helpers/paging/pageable.ts";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { RolesComponent, RolesPage } from "@/pages/roles.page.tsx";
import { RoleActions } from "@/sys/role/role.actions.ts";

const tableMocks = vi.hoisted(() => {
  const mockRole = {
    code: "ADMIN",
    createdAt: 1704067200000,
    description: "role.admin",
    id: "r1",
    name: "Admin",
    permissionIds: [] as string[],
    status: 1 as RoleStatus,
  };
  return {
    loadData: vi.fn().mockResolvedValue(undefined),
    mockRole,
    records: [mockRole],
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
      query: {},
      records: tableMocks.records,
      setPageable: vi.fn(),
      setPaging: vi.fn(),
      setQuery: vi.fn(),
      setRecords: tableMocks.setRecords,
    }),
  };
});

vi.mock("@/sys/role/role.actions.ts", () => ({
  RoleActions: {
    delete: vi.fn(),
    list: vi.fn(),
    updateStatus: vi.fn(),
  },
}));

vi.mock("@/pages/role/role.edit.dialog.tsx", () => ({
  RoleEditDialog: ({ open }: { open: boolean }) => (open ? <div>role dialog</div> : null),
}));

describe("<RolesComponent />", () => {
  beforeEach(() => {
    tableMocks.records = [tableMocks.mockRole];
    vi.clearAllMocks();
  });

  afterEach(() => vi.resetAllMocks());

  it("should render role table rows", () => {
    render(<RolesComponent />);

    expect(screen.getByText("role.title.")).toBeInTheDocument();
    expect(screen.getByText("ADMIN")).toBeInTheDocument();
    expect(screen.getByText("Admin")).toBeInTheDocument();
  });

  it("should open edit dialog when add is clicked", async () => {
    render(<RolesComponent />);

    await userEvent.click(screen.getByRole("button", { name: /action.add/i }));

    expect(screen.getByText("role dialog")).toBeInTheDocument();
  });

  it("should update status when status switch is toggled", async () => {
    vi.mocked(RoleActions.updateStatus).mockResolvedValue(true);

    render(<RolesComponent />);

    await userEvent.click(screen.getByRole("switch"));

    await waitFor(() =>
      expect(RoleActions.updateStatus).toHaveBeenCalledWith("r1", RoleStatus.INACTIVE),
    );
    expect(tableMocks.setRecords).toHaveBeenCalled();
  });

  it("should delete selected roles when bulk delete succeeds", async () => {
    vi.mocked(RoleActions.delete).mockResolvedValue(true);

    render(<RolesComponent />);

    await userEvent.click(screen.getByLabelText("Select row"));
    await userEvent.click(screen.getByRole("button", { name: "action.delete" }));
    await userEvent.click(screen.getByRole("button", { name: "action.continue" }));

    await waitFor(() => expect(RoleActions.delete).toHaveBeenCalledWith(["r1"]));
    expect(tableMocks.loadData).toHaveBeenCalled();
  });
});

let mockParams = new URLSearchParams();

vi.mock("react-router", () => ({
  useSearchParams: () => [mockParams, vi.fn()],
}));

describe("<RolesPage />", () => {
  it("should load roles when page mounts", async () => {
    mockParams = new URLSearchParams();
    vi.mocked(RoleActions.list).mockResolvedValue({
      paging: DEFAULT_PAGING,
      records: [tableMocks.mockRole],
    });

    render(<RolesPage />);

    await waitFor(() => expect(RoleActions.list).toHaveBeenCalled());
  });
});
