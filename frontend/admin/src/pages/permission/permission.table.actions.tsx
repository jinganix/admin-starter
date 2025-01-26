import { CloudUploadIcon, RefreshCcwIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button.tsx";
import { TableActions } from "@/components/table/table.actions.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { AuthorityActions } from "@/sys/authority/authority.actions.ts";
import { Permission, PermissionQuery } from "@/sys/permission/permission.types";

export const PermissionTableActions: FC = () => {
  const { t } = useTranslation();

  const { loadData } = useTableData<PermissionQuery, Permission>();

  const [uploading, onUpload] = useLoading(async () => {
    if (await AuthorityActions.uploadUI()) {
      await loadData();
    }
  }, false);
  const [reloading, onReload] = useLoading(async () => {
    if (await AuthorityActions.reloadAPI()) {
      await loadData();
    }
  }, false);

  return (
    <TableActions>
      <Button
        variant="outline"
        disabled={uploading}
        className="h-8"
        onClick={() => void onUpload()}
      >
        <Spinner loading={uploading} />
        <span>{t("permission.action.syncUI")}</span> <CloudUploadIcon />
      </Button>

      <Button
        variant="outline"
        disabled={reloading}
        className="h-8"
        onClick={() => void onReload()}
      >
        <Spinner loading={reloading} />
        <span>{t("permission.action.reloadApi")}</span> <RefreshCcwIcon />
      </Button>
    </TableActions>
  );
};
