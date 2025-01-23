import { Column } from "@tanstack/react-table";
import {
  ArrowDownUpIcon,
  ChevronsUpDownIcon,
  EyeOffIcon,
  SortAscIcon,
  SortDescIcon,
} from "lucide-react";
import { HTMLAttributes, ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";
import { cn } from "@/helpers/lib/cn";

interface Props<TData, TValue> extends HTMLAttributes<HTMLDivElement> {
  column: Column<TData, TValue>;
  i18nKey: (id: string) => string;
}

export function TableColumnHeader<TData, TValue>({
  column,
  className,
  i18nKey,
}: Props<TData, TValue>): ReactNode {
  const { t } = useTranslation();
  const title = t(i18nKey(column.id));

  if (!column.getCanSort()) {
    return (
      <div className={cn("flex items-center justify-center space-x-2", className)}>{title}</div>
    );
  }

  return (
    <div className={cn("flex items-center justify-center space-x-2", className)}>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" size="sm" className="h-8 data-[state=open]:bg-accent">
            <span>{title}</span>
            {column.getIsSorted() === "desc" ? (
              <SortDescIcon className="ml-2 size-4" />
            ) : column.getIsSorted() === "asc" ? (
              <SortAscIcon className="ml-2 size-4" />
            ) : (
              <ChevronsUpDownIcon className="ml-2 size-4" />
            )}
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start">
          <DropdownMenuItem onClick={() => column.toggleSorting(false, column.getCanMultiSort())}>
            <SortAscIcon className="text-muted-foreground/70 mr-2 size-3.5" />
            {t("table.header.menu.asc")}
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => column.toggleSorting(true, column.getCanMultiSort())}>
            <SortDescIcon className="text-muted-foreground/70 mr-2 size-3.5" />
            {t("table.header.menu.desc")}
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => column.clearSorting()}>
            <ArrowDownUpIcon className="text-muted-foreground/70 mr-2 size-3.5" />
            {t("table.header.menu.unsorted")}
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem onClick={() => column.toggleVisibility(false)}>
            <EyeOffIcon className="text-muted-foreground/70 mr-2 size-3.5" />
            {t("table.header.menu.hide")}
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
