import { ArrowBigRightIcon } from "lucide-react";
import { ChangeEvent, KeyboardEvent, ReactNode, useState } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button.tsx";
import { Input } from "@/components/shadcn/input.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";

export function TablePageGoto(): ReactNode {
  const { t } = useTranslation();
  const { loading, paging, pageable, setPageable } = useTableData();
  const [goto, setGoto] = useState("");

  const setPage = (page: number) => void setPageable({ ...pageable, page });

  const onGoto = (): void => {
    goto && setPage(Math.max(0, Math.min(paging.pages - 1, Number(goto) - 1)));
    setGoto("");
  };

  const onKeyDown = (event: KeyboardEvent) => void (event.key === "Enter" && onGoto());

  const onGotoChange = (event: ChangeEvent<{ value: string }>): void => setGoto(event.target.value);

  return (
    <div className="flex items-center justify-between space-x-1 md:space-x-2">
      <Input
        value={goto}
        className="h-8 w-12 md:w-16 px-1"
        type="number"
        onChange={onGotoChange}
        onKeyDown={onKeyDown}
      />
      <Button
        disabled={loading}
        variant="outline"
        className="h-8 px-1 hidden md:flex"
        onClick={onGoto}
      >
        {t("table.footer.goto")}
      </Button>
      <Button disabled={loading} variant="outline" className="h-8 w-8 md:hidden" onClick={onGoto}>
        <ArrowBigRightIcon />
      </Button>
    </div>
  );
}
