import {
  DEFAULT_PAGE,
  DEFAULT_PAGEABLE,
  DEFAULT_PAGING,
  SortDirection,
} from "@helpers/paging/pageable.ts";
import { toSearchParams } from "@helpers/search.params.ts";
import { DataLoader } from "@helpers/table/table.types.ts";
import { act, render, renderHook, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi, type Mock } from "vitest";
import { TableDataProvider, useTableData } from "@/components/table/table.data.context.tsx";

const setParams = vi.fn();
let mockParams = new URLSearchParams();

vi.mock("react-router", () => ({
  useSearchParams: () => [mockParams, setParams],
}));

type Row = { id: string };
type Query = { q?: string };

function createLoadData(records: Row[] = [{ id: "a" }]): Mock<DataLoader<Query, Row>> {
  return vi.fn().mockResolvedValue({
    paging: { page: 0, pages: 1, size: 10, total: records.length },
    records,
  });
}

function Harness(): React.ReactElement {
  const {
    loading,
    records,
    pageable,
    paging,
    setQuery,
    setPageable,
    loadData,
    setRecords,
    setPaging,
  } = useTableData<Query, Row>();

  return (
    <div>
      <span data-testid="loading">{String(loading)}</span>
      <span data-testid="records">{records.length}</span>
      <span data-testid="page">{pageable.page}</span>
      <span data-testid="total">{paging.total}</span>
      <button type="button" onClick={() => void setQuery({ q: "next" })}>
        setQuery
      </button>
      <button
        type="button"
        onClick={() =>
          void setPageable({ ...pageable, page: 2, size: pageable.size, sort: pageable.sort })
        }
      >
        setPageable
      </button>
      <button type="button" onClick={() => void loadData()}>
        reload
      </button>
      <button type="button" onClick={() => setRecords([{ id: "a" }, { id: "b" }])}>
        setRecords
      </button>
      <button type="button" onClick={() => setPaging({ page: 0, pages: 9, size: 10, total: 99 })}>
        setPaging
      </button>
    </div>
  );
}

describe("TableDataProvider", () => {
  beforeEach(() => {
    mockParams = new URLSearchParams();
    setParams.mockClear();
    vi.useRealTimers();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  it("should honor initial pageable paging and query props", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider
        loadData={loadData}
        pageable={{ page: 1, size: 20, sort: { id: SortDirection.asc } }}
        paging={{ page: 1, pages: 2, size: 20, total: 5 }}
        query={{ q: "init" }}
      >
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("page")).toHaveTextContent("1"));
    expect(loadData).toHaveBeenCalledWith(expect.objectContaining({ page: 1, size: 20 }), {
      q: "init",
    });
  });

  it("should load records when provider mounts", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));
    expect(loadData).toHaveBeenCalledWith(
      expect.objectContaining({ page: DEFAULT_PAGE, size: 10 }),
      {},
    );
  });

  it("should reset page when setQuery is called", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider
        loadData={loadData}
        pageable={{ page: 3, size: 10, sort: { createdAt: SortDirection.desc } }}
      >
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("page")).toHaveTextContent("3"));

    await userEvent.click(screen.getByRole("button", { name: "setQuery" }));

    await waitFor(() => expect(screen.getByTestId("page")).toHaveTextContent("0"));
    expect(loadData).toHaveBeenLastCalledWith(expect.objectContaining({ page: DEFAULT_PAGE }), {
      q: "next",
    });
  });

  it("should load data when setPageable is called", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));
    loadData.mockClear();

    await userEvent.click(screen.getByRole("button", { name: "setPageable" }));

    await waitFor(() => expect(screen.getByTestId("page")).toHaveTextContent("2"));
    expect(loadData).toHaveBeenCalledWith(expect.objectContaining({ page: 2 }), {});
  });

  it("should clear records when load returns null", async () => {
    const loadData = vi
      .fn()
      .mockResolvedValueOnce({
        paging: { page: 0, pages: 1, size: 10, total: 1 },
        records: [{ id: "a" }],
      })
      .mockResolvedValueOnce(null);

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));

    await userEvent.click(screen.getByRole("button", { name: "reload" }));

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("0"));
  });

  it("should sync search params when pageable or query changes", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider loadData={loadData} query={{ q: "init" }}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(setParams).toHaveBeenCalled());
    const synced = setParams.mock.calls[setParams.mock.calls.length - 1]?.[0] as URLSearchParams;
    expect(synced.get("page")).toBe("0");
    expect(synced.get("size")).toBe("10");
    expect(synced.get("q")).toBe("init");
  });

  it("should not update search params when url already matches pageable and query", async () => {
    const loadData = createLoadData();
    mockParams = toSearchParams({
      page: DEFAULT_PAGE,
      size: 10,
      sort: { createdAt: SortDirection.desc },
    });

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));
    expect(setParams).not.toHaveBeenCalled();
  });

  it("should allow setRecords and setPaging from context", async () => {
    const loadData = createLoadData();

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));

    await userEvent.click(screen.getByRole("button", { name: "setRecords" }));
    expect(screen.getByTestId("records")).toHaveTextContent("2");

    await userEvent.click(screen.getByRole("button", { name: "setPaging" }));
    expect(screen.getByTestId("total")).toHaveTextContent("99");
  });

  it("should reload records when loadData is invoked from context", async () => {
    const loadData = vi
      .fn()
      .mockResolvedValueOnce({
        paging: { page: 0, pages: 1, size: 10, total: 1 },
        records: [{ id: "a" }],
      })
      .mockResolvedValueOnce({
        paging: { page: 0, pages: 1, size: 10, total: 2 },
        records: [{ id: "a" }, { id: "b" }],
      });

    render(
      <TableDataProvider loadData={loadData}>
        <Harness />
      </TableDataProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("1"));

    await userEvent.click(screen.getByRole("button", { name: "reload" }));

    await waitFor(() => expect(screen.getByTestId("records")).toHaveTextContent("2"));
    expect(loadData).toHaveBeenCalledTimes(2);
  });
});

describe("useTableData", () => {
  it("should return default context value outside provider", async () => {
    const { result } = renderHook(() => useTableData());

    expect(result.current.records).toEqual([]);
    expect(result.current.loading).toBe(false);
    await expect(result.current.loadData()).resolves.toBe(true);
    await expect(result.current.setPageable(DEFAULT_PAGEABLE)).resolves.toBe(true);
    await expect(result.current.setQuery({})).resolves.toBe(true);
    act(() => {
      result.current.setPaging(DEFAULT_PAGING);
      result.current.setRecords([]);
    });
  });
});
