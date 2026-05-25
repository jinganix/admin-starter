import { DEFAULT_PAGEABLE, DEFAULT_PAGING } from "@helpers/paging/pageable.ts";
import { UserStatus } from "@proto/SysUserProto.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { UsersComponent, UsersPage } from "@/pages/users.page.tsx";
import { UserActions } from "@/sys/user/user.actions.ts";

const tableMocks = vi.hoisted(() => {
  const mockUser = {
    createdAt: 1704067200000,
    id: "u1",
    nickname: "Alice",
    status: 1 as UserStatus,
    username: "alice",
  };
  return {
    loadData: vi.fn().mockResolvedValue(undefined),
    mockUser,
    records: [mockUser],
    setQuery: vi.fn().mockResolvedValue(undefined),
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
      setQuery: tableMocks.setQuery,
      setRecords: tableMocks.setRecords,
    }),
  };
});

vi.mock("@/sys/store.context.tsx", () => ({
  useCondStore: () => ({ satisfy: () => true }),
}));

vi.mock("@/sys/user/user.actions.ts", () => ({
  UserActions: {
    delete: vi.fn(),
    list: vi.fn(),
    updateStatus: vi.fn(),
  },
}));

vi.mock("@/pages/user/user.create.dialog.tsx", () => ({
  UserCreateDialog: ({ open }: { open: boolean }) => (open ? <div>create dialog</div> : null),
}));
vi.mock("@/pages/user/user.update.dialog.tsx", () => ({
  UserUpdateDialog: ({ open }: { open: boolean }) => (open ? <div>update dialog</div> : null),
}));

describe("<UsersComponent />", () => {
  beforeEach(() => {
    tableMocks.records = [tableMocks.mockUser];
    vi.clearAllMocks();
  });

  afterEach(() => vi.resetAllMocks());

  it("should render user table rows", () => {
    render(<UsersComponent />);

    expect(screen.getByText("user.title.")).toBeInTheDocument();
    expect(screen.getByText("alice")).toBeInTheDocument();
    expect(screen.getByText("Alice")).toBeInTheDocument();
  });

  it("should open create dialog when add is clicked", async () => {
    render(<UsersComponent />);

    await userEvent.click(screen.getByRole("button", { name: /action.add/i }));

    expect(screen.getByText("create dialog")).toBeInTheDocument();
  });

  it("should update status when status switch is toggled", async () => {
    vi.mocked(UserActions.updateStatus).mockResolvedValue(true);

    render(<UsersComponent />);

    await userEvent.click(screen.getByRole("switch"));

    await waitFor(() =>
      expect(UserActions.updateStatus).toHaveBeenCalledWith("u1", UserStatus.INACTIVE),
    );
    expect(tableMocks.setRecords).toHaveBeenCalled();
  });

  it("should delete selected users when bulk delete succeeds", async () => {
    vi.mocked(UserActions.delete).mockResolvedValue(true);

    render(<UsersComponent />);

    await userEvent.click(screen.getByLabelText("Select row"));
    await userEvent.click(screen.getByRole("button", { name: "action.delete" }));
    await userEvent.click(screen.getByRole("button", { name: "action.continue" }));

    await waitFor(() => expect(UserActions.delete).toHaveBeenCalledWith(["u1"]));
    expect(tableMocks.loadData).toHaveBeenCalled();
  });

  it("should not reload when delete fails", async () => {
    vi.mocked(UserActions.delete).mockResolvedValue(false);

    render(<UsersComponent />);

    await userEvent.click(screen.getByLabelText("Select row"));
    await userEvent.click(screen.getByRole("button", { name: "action.delete" }));
    await userEvent.click(screen.getByRole("button", { name: "action.continue" }));

    await waitFor(() => expect(UserActions.delete).toHaveBeenCalled());
    expect(tableMocks.loadData).not.toHaveBeenCalled();
  });
});

const setParams = vi.fn();
let mockParams = new URLSearchParams();

vi.mock("react-router", () => ({
  useSearchParams: () => [mockParams, setParams],
}));

describe("<UsersPage />", () => {
  it("should load users with query from search params", async () => {
    mockParams = new URLSearchParams("size=10&userId=1&username=alice");
    vi.mocked(UserActions.list).mockResolvedValue({
      paging: DEFAULT_PAGING,
      records: [tableMocks.mockUser],
    });

    render(<UsersPage />);

    await waitFor(() => expect(UserActions.list).toHaveBeenCalled());
  });
});
