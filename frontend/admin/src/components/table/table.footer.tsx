import { Paging } from "@helpers/paging/paging.ts";
import { getPageSize, mergeParams } from "@helpers/search.params.ts";
import { ArrowBigRightIcon } from "lucide-react";
import { observer } from "mobx-react-lite";
import { ChangeEvent, FC, KeyboardEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button";
import { Input } from "@/components/shadcn/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/shadcn/select";
import { TablePagination } from "@/components/table/table.pagination.tsx";
import { useTableParams } from "@/components/table/table.params.context.tsx";
import { cn } from "@/helpers/lib/cn";

interface Props {
  paging: Paging;
  className?: string;
}

export const TableFooter: FC<Props> = observer(({ paging, className }) => {
  const { t } = useTranslation();
  const [goto, setGoto] = useState("");
  const [params, setParams] = useTableParams();
  const setPage = (page: number): void => setParams(mergeParams(params, { page }));
  const setSize = (size: number): void => setParams(mergeParams(params, { size }));

  const onGoto = async (): Promise<void> => {
    goto && setPage(Math.max(0, Math.min(paging.pages - 1, Number(goto) - 1)));
    setGoto("");
  };

  const handleKeyDown = async (event: KeyboardEvent): Promise<void> => {
    if (event.key === "Enter") {
      await onGoto();
    }
  };

  const onGotoChange = (event: ChangeEvent<{ value: string }>): void => {
    setGoto(event.target.value);
  };

  const pageSize = getPageSize(params);

  return (
    <div className={cn("flex flex-col xl:flex-row items-center justify-between gap-4", className)}>
      <div className="w-full xl:w-auto flex items-center justify-between">
        <div className="flex items-center justify-center text-sm font-medium whitespace-nowrap">
          {t("table.footer.totalRecords")}: {paging.total}
        </div>
        <div className="xl:hidden text-sm font-medium whitespace-nowrap">
          {t("table.footer.page", { page: paging.page + 1, total: paging.pages })}
        </div>
      </div>

      <div className="flex items-center w-full justify-between md:justify-end space-x-3 xl:space-x-6">
        <div className="flex items-center justify-between space-x-1 md:space-x-2">
          <Input
            value={goto}
            className="h-8 w-12 md:w-16 px-1"
            type="number"
            onChange={onGotoChange}
            onKeyDown={handleKeyDown}
          />
          <Button variant="outline" className="h-8 px-1 hidden md:flex" onClick={onGoto}>
            {t("table.footer.goto")}
          </Button>
          <Button variant="outline" className="h-8 w-8 md:hidden" onClick={onGoto}>
            <ArrowBigRightIcon />
          </Button>
        </div>

        <div className="flex items-center space-x-2 font-medium">
          <div className="text-sm whitespace-nowrap hidden xl:block">
            {t("table.footer.rowsPerPage")}
          </div>
          <Select value={`${pageSize}`} onValueChange={(value) => setSize(Number(value))}>
            <SelectTrigger className="h-8 w-auto">
              <SelectValue placeholder={pageSize} />
            </SelectTrigger>
            <SelectContent side="top">
              {[10, 20, 30, 40, 50].map((x) => (
                <SelectItem key={x} value={`${x}`}>
                  {x}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <TablePagination paging={paging} />
      </div>
    </div>
  );
});
