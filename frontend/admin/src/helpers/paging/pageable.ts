export enum SortDirection {
  asc = "asc",
  desc = "desc",
}

export const DEFAULT_PAGE = 0;
export const DEFAULT_PAGE_SIZE = 10;

export class Pageable {
  page = DEFAULT_PAGE;
  size = DEFAULT_PAGE_SIZE;
  sort: Record<string, SortDirection> = {};
}
