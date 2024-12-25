import { keyBy, mapValues, split } from "lodash";
import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE, Pageable, SortDirection } from "./paging/pageable";

const skipPredicate = (x: unknown): boolean =>
  x === null || x === undefined || typeof x === "function";

export function overrideParams(params: URLSearchParams, data: object): URLSearchParams {
  return mergeParams(params, data, () => false);
}

export function mergeParams(
  params: URLSearchParams,
  data: object,
  skip = skipPredicate,
): URLSearchParams {
  const newParams = new URLSearchParams(params);
  for (const [key, value] of Object.entries(data)) {
    if (skip && skip(value)) {
      continue;
    }
    if (!value) {
      newParams.delete(key);
      continue;
    }
    let str;
    if (Array.isArray(value)) {
      str = value.join(",");
    } else if (typeof value === "object") {
      str = Object.entries(value)
        .map(([key, value]) => `${key},${value}`)
        .join(";");
    } else {
      str = String(value);
    }
    str ? newParams.set(key, str) : newParams.delete(key);
  }
  return newParams;
}

export function toSearchParams(data: object): URLSearchParams {
  return mergeParams(new URLSearchParams(), data);
}

export function toPageable(params: URLSearchParams): Pageable {
  const pageable = new Pageable();
  pageable.page = getPage(params);
  pageable.size = getPageSize(params);
  const sorts = (params.get("sort") || "")
    .split(";")
    .map((x) => {
      const [name, dir] = split(x, ",");
      return { direction: SortDirection[dir as keyof typeof SortDirection], name };
    })
    .filter(({ name }) => Boolean(name));
  pageable.sort = mapValues(keyBy(sorts, "name"), "direction");
  return pageable;
}

export function defaultSearchParams(
  sort: Record<string, SortDirection> = { createdAt: SortDirection.desc },
  page = DEFAULT_PAGE,
  size = DEFAULT_PAGE_SIZE,
): URLSearchParams {
  return mergeParams(new URLSearchParams(), { page, size, sort });
}

type ResolverDef<T> = {
  [K in keyof T]: [T[K], (x: string) => T[K]];
};

export class FormValuesResolver<T extends Record<string, unknown>> {
  constructor(private defs: ResolverDef<T>) {}

  resolve(params?: URLSearchParams): T {
    const data = {} as T;
    for (const [key, [deft, mapper]] of Object.entries(this.defs)) {
      data[key as keyof T] = params && params.has(key) ? mapper(params.get(key)) : deft;
    }
    return data;
  }
}

export function getPage(params: URLSearchParams): number {
  return params.has("page") ? Number(params.get("page")) : DEFAULT_PAGE;
}

export function getPageSize(params: URLSearchParams): number {
  return params.has("size") ? Number(params.get("size")) : DEFAULT_PAGE_SIZE;
}
