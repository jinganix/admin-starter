import { DEFAULT_PAGEABLE, DEFAULT_PAGING } from "@helpers/paging/pageable.ts";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AuditsComponent, AuditsPage } from "@/pages/audits.page.tsx";
import { AuditActions } from "@/sys/audit/audit.actions.ts";

const tableMocks = vi.hoisted(() => {
  const mockAudit = {
    createdAt: 1704067200000,
    id: "a1",
    method: "GET",
    path: "/api/users",
    userId: "u1",
    username: "alice",
  };
  return {
    mockAudit,
    query: {} as { userId?: string; username?: string; path?: string; method?: string },
    records: [mockAudit],
    setQuery: vi.fn(),
  };
});

vi.mock("@/components/table/table.data.context.tsx", async (importActual) => {
  const actual = await importActual<typeof import("@/components/table/table.data.context.tsx")>();
  return {
    ...actual,
    useTableData: () => ({
      loadData: vi.fn(),
      loading: false,
      pageable: DEFAULT_PAGEABLE,
      paging: DEFAULT_PAGING,
      query: tableMocks.query,
      records: tableMocks.records,
      setPageable: vi.fn(),
      setPaging: vi.fn(),
      setQuery: tableMocks.setQuery,
      setRecords: vi.fn(),
    }),
  };
});

vi.mock("@/sys/audit/audit.actions.ts", () => ({
  AuditActions: { list: vi.fn() },
}));

describe("<AuditsComponent />", () => {
  beforeEach(() => {
    tableMocks.records = [tableMocks.mockAudit];
    tableMocks.query = {};
    vi.clearAllMocks();
  });

  afterEach(() => vi.resetAllMocks());

  it("should render audit rows", () => {
    render(<AuditsComponent />);

    expect(screen.getByText("audit.title.")).toBeInTheDocument();
    expect(screen.getByText("alice")).toBeInTheDocument();
    expect(screen.getByText("/api/users")).toBeInTheDocument();
    expect(screen.getByText("GET")).toBeInTheDocument();
  });

  it("should show deleted label when username is missing", () => {
    tableMocks.records = [{ ...tableMocks.mockAudit, username: "" }];

    render(<AuditsComponent />);

    expect(screen.getByText("[audit.deleted]")).toBeInTheDocument();
  });

  it("should set query when filterable cells are clicked", async () => {
    render(<AuditsComponent />);

    await userEvent.click(screen.getByText("u1"));
    expect(tableMocks.setQuery).toHaveBeenCalledWith({ userId: "u1" });

    await userEvent.click(screen.getByText("alice"));
    expect(tableMocks.setQuery).toHaveBeenCalledWith({ username: "alice" });

    await userEvent.click(screen.getByText("/api/users"));
    expect(tableMocks.setQuery).toHaveBeenCalledWith({ path: "/api/users" });

    await userEvent.click(screen.getByText("GET"));
    expect(tableMocks.setQuery).toHaveBeenCalledWith({ method: "GET" });
  });
});

let mockParams = new URLSearchParams();

vi.mock("react-router", () => ({
  useSearchParams: () => [mockParams, vi.fn()],
}));

describe("<AuditsPage />", () => {
  it("should load audits when page mounts", async () => {
    mockParams = new URLSearchParams();
    vi.mocked(AuditActions.list).mockResolvedValue({
      paging: DEFAULT_PAGING,
      records: [tableMocks.mockAudit],
    });

    render(<AuditsPage />);

    await waitFor(() => expect(AuditActions.list).toHaveBeenCalled());
  });
});
