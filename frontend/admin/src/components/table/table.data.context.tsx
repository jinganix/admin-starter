import { Replay } from "@helpers/network/replay.ts";
import { DEFAULT_PAGEABLE, DEFAULT_PAGING, Pageable, Paging } from "@helpers/paging/pageable.ts";
import { paramsEquals, toSearchParams } from "@helpers/search.params.ts";
import { DataLoader, DataRecords } from "@helpers/table/table.types.ts";
import { isEqual } from "lodash";
import { Context, createContext, ReactNode, useContext, useEffect, useState } from "react";
import { useSearchParams } from "react-router";
import { useLoading } from "@/hooks/use.loading.ts";

type ContextValue<Query = unknown, T = unknown> = {
  pageable: Pageable;
  setPageable: (pageable: Pageable) => Promise<boolean>;
  query: Query;
  setQuery: (query: Query) => Promise<boolean>;
  loadData: () => Promise<boolean>;
  loading: boolean;
  paging: Paging;
  setPaging: (paging: Paging) => void;
  records: T[];
  setRecords: (records: T[]) => void;
};

const TableDataContext = createContext<ContextValue>({
  loadData: async () => true,
  loading: false,
  pageable: DEFAULT_PAGEABLE,
  paging: DEFAULT_PAGING,
  query: {},
  records: [],
  setPageable: async () => true,
  setPaging: () => {},
  setQuery: async () => true,
  setRecords: () => {},
});

type Props<Query, T> = {
  children: ReactNode;
  loadData: DataLoader<Query, T>;
  pageable?: Pageable;
  paging?: Paging;
  query?: Query;
};

type CachedQuery<Query> = {
  pageable: Pageable;
  query: Query | null;
};

class MemorizedDataLoader<Query, T> {
  loadData: DataLoader<Query, T>;
  loadedAt = 0;
  query: CachedQuery<Query> | null = null;
  replay = new Replay<DataRecords<T> | null>();

  constructor(loadData: DataLoader<Query, T>) {
    this.loadData = loadData;
  }

  async load(pageable: Pageable, query: Query): Promise<DataRecords<T> | null> {
    if (this.useCache(pageable, query)) {
      return this.replay.value();
    }
    this.replay = new Replay();
    await this.replay.resolve(() => this.loadData(pageable, query));
    return this.replay.value();
  }

  private useCache(pageable: Pageable, query: Query): boolean {
    const cachedQuery = { pageable, query };
    if (isEqual(this.query, cachedQuery) && (this.loadedAt ?? 0) + 100 > Date.now()) {
      return true;
    }
    this.query = cachedQuery;
    this.loadedAt = Date.now();
    return false;
  }
}

export function TableDataProvider<Query, T>({
  children,
  loadData: defaultLoadData,
  pageable: defaultPageable = DEFAULT_PAGEABLE,
  paging: defaultPaging = DEFAULT_PAGING,
  query: defaultQuery = {} as Query,
}: Props<Query, T>): ReactNode {
  const [params, setParams] = useSearchParams();
  const [pageable, setPageable] = useState(defaultPageable);
  const [query, setQuery] = useState<Query>(defaultQuery);
  const [paging, setPaging] = useState(defaultPaging);
  const [records, setRecords] = useState<T[]>([]);
  const loader = new MemorizedDataLoader(defaultLoadData);
  const [loading, loadData] = useLoading(
    (pageable: Pageable, query: Query) => loader.load(pageable, query),
    false,
  );

  useEffect(() => void asyncLoadData(pageable, query), []);

  useEffect(() => {
    const newParams = toSearchParams({ ...pageable, ...query });
    if (!paramsEquals(newParams, params)) {
      setParams(newParams);
    }
  }, [pageable, query]);

  const asyncSetQuery = async (query: Query): Promise<boolean> => {
    setQuery(query);
    return await asyncLoadData(pageable, query);
  };

  const asyncSetPageable = async (pageable: Pageable): Promise<boolean> => {
    setPageable(pageable);
    return await asyncLoadData(pageable, query);
  };

  const asyncLoadData = async (pageable: Pageable, query: Query): Promise<boolean> => {
    const res = await loadData(pageable, query);
    if (res) {
      setPaging(res.paging);
      setRecords(res.records);
    } else {
      setRecords([]);
    }
    return !!res;
  };

  const value = {
    loadData: () => asyncLoadData(pageable, query),
    loading,
    pageable,
    paging,
    query,
    records,
    setPageable: asyncSetPageable,
    setPaging,
    setQuery: asyncSetQuery,
    setRecords,
  } satisfies ContextValue<Query, T> as ContextValue;

  return <TableDataContext.Provider value={value}>{children}</TableDataContext.Provider>;
}

export function useTableData<Query, T = unknown>(): ContextValue<Query, T> {
  return useContext(TableDataContext as Context<ContextValue<Query, T>>);
}
