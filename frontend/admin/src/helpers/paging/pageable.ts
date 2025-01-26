import { IPagingPb } from "@proto/PageableProto.ts";

export enum SortDirection {
  asc = "asc",
  desc = "desc",
}

export const DEFAULT_PAGE = 0;
export const DEFAULT_PAGE_SIZE = 10;

export interface Pageable {
  page: number;
  size: number;
  sort: Record<string, SortDirection>;
}

export interface Paging {
  page: number;
  size: number;
  pages: number;
  total: number;
}

export const DEFAULT_PAGEABLE: Pageable = {
  page: DEFAULT_PAGE,
  size: DEFAULT_PAGE_SIZE,
  sort: { createdAt: SortDirection.desc },
};

export const DEFAULT_PAGING: Paging = {
  page: DEFAULT_PAGE,
  pages: 0,
  size: DEFAULT_PAGE_SIZE,
  total: 0,
};

export function pbToPaging(pb: IPagingPb): Paging {
  return {
    page: pb.page,
    pages: pb.pages,
    size: pb.size,
    total: pb.total,
  };
}
