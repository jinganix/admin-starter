import { FC } from "react";
import { useTranslation } from "react-i18next";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { TablePageGoto } from "@/components/table/table.page.goto.tsx";
import { TablePageSize } from "@/components/table/table.page.size.tsx";
import { TablePagination } from "@/components/table/table.pagination.tsx";
import { cn } from "@/helpers/lib/cn";

interface Props {
  className?: string;
}

export const TableFooter: FC<Props> = ({ className }) => {
  const { t } = useTranslation();
  const { paging } = useTableData();

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
        <TablePageGoto />

        <TablePageSize />

        <TablePagination />
      </div>
    </div>
  );
};
