import { CloudUploadIcon, RefreshCcwIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button.tsx";
import { TableActions } from "@/components/table/table.actions.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { reloadAuthorities, uploadAuthorities } from "@/sys/authority/authority.actions.ts";

export const PermissionTableActions: FC = () => {
  const { t } = useTranslation();

  const [uploading, onUpload] = useLoading(uploadAuthorities, false);
  const [reloading, onReload] = useLoading(reloadAuthorities, false);

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
