import { ReactNode } from "react";
import { useTranslation } from "react-i18next";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/shadcn/select.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";

export function TablePageSize(): ReactNode {
  const { t } = useTranslation();
  const { loading, pageable, setPageable } = useTableData();

  const setSize = (size: number) => void setPageable({ ...pageable, size });

  return (
    <div className="flex items-center space-x-2 font-medium">
      <div className="text-sm whitespace-nowrap hidden xl:block">
        {t("table.footer.rowsPerPage")}
      </div>
      <Select
        disabled={loading}
        value={`${pageable.size}`}
        onValueChange={(value) => setSize(Number(value))}
      >
        <SelectTrigger className="h-8 w-auto">
          <SelectValue placeholder={pageable.size} />
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
  );
}
