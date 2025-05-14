import {
  ChevronLeftIcon,
  ChevronRightIcon,
  ChevronsLeftIcon,
  ChevronsRightIcon,
  MoreHorizontalIcon,
} from "lucide-react";
import { FC } from "react";
import { Button } from "@/components/shadcn/button";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { cn } from "@/helpers/lib/cn";

const ELLIPSIS = -1;
type PageNumber = number;

interface PaginationItem {
  href: string | undefined;
  key: string;
  page: number;
  selected: boolean;
}

function pageNumbers(page: number, totalPage: number): PageNumber[] {
  if (totalPage <= 7) {
    return Array.from(Array(totalPage).keys()).map((i) => i + 1);
  }
  if (page < 3) {
    return [1, 2, 3, ELLIPSIS, totalPage - 2, totalPage - 1, totalPage];
  } else if (page === 3) {
    return [1, 2, 3, 4, ELLIPSIS, totalPage - 1, totalPage];
  } else if (page > totalPage - 2) {
    return [1, 2, 3, ELLIPSIS, totalPage - 2, totalPage - 1, totalPage];
  } else if (page === totalPage - 2) {
    return [1, 2, ELLIPSIS, totalPage - 3, totalPage - 2, totalPage - 1, totalPage];
  } else {
    return [1, ELLIPSIS, page - 1, page, page + 1, ELLIPSIS, totalPage];
  }
}

export function paginationData(page: number, totalPage: number): PaginationItem[] {
  return pageNumbers(page, totalPage).map((number, index) => ({
    href: number === ELLIPSIS ? undefined : `${number}`,
    key: `${index}`,
    page: number,
    selected: number === page,
  }));
}

export const TablePagination: FC = () => {
  const { pageable, setPageable, loading, paging } = useTableData();
  const setPage = (page: number) => void setPageable({ ...pageable, page });

  const page = pageable.page;
  const { pages } = paging;
  const data = paginationData(page + 1 || 1, pages || 1);

  return (
    <div className="flex items-center">
      <nav className="relative z-0 inline-flex space-x-1 rounded-md">
        <Button
          variant="outline"
          className="h-8 min-w-8 p-0.5"
          onClick={() => setPage(0)}
          disabled={loading || page <= 0}
        >
          <ChevronsLeftIcon className="size-4" />
        </Button>

        <Button
          variant="outline"
          className="h-8 min-w-8 p-0.5"
          onClick={() => setPage(Math.max(0, page - 1))}
          disabled={loading || page <= 0}
        >
          <ChevronLeftIcon className="size-4" />
        </Button>

        {data.map((item, index) => (
          <Button
            key={index}
            variant="outline"
            className={cn("hidden xl:block h-8 min-w-8 p-0.5", {
              "border-2 border-primary text-primary": page === item.page - 1,
            })}
            onClick={() => setPage(item.page - 1)}
            disabled={loading || item.page === ELLIPSIS}
          >
            {item.page === ELLIPSIS ? <MoreHorizontalIcon /> : item.page}
          </Button>
        ))}

        <Button
          variant="outline"
          className="h-8 min-w-8 p-0.5"
          onClick={() => setPage(Math.min(pages, page + 1))}
          disabled={loading || page >= pages - 1}
        >
          <ChevronRightIcon className="size-4" />
        </Button>

        <Button
          variant="outline"
          className="h-8 min-w-8 p-0.5"
          onClick={() => setPage(pages - 1)}
          disabled={loading || page >= pages - 1}
        >
          <ChevronsRightIcon className="size-4" />
        </Button>
      </nav>
    </div>
  );
};
