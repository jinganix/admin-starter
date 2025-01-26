import { Pageable, Paging } from "@helpers/paging/pageable.ts";

export type DataRecords<T> = { records: T[]; paging: Paging };
export type DataLoader<Query, T> = (
  pageable: Pageable,
  query: Query,
) => Promise<DataRecords<T> | null>;
