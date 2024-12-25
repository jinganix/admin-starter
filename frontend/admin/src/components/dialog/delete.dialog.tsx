import { FC } from "react";
import { useTranslation } from "react-i18next";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/shadcn/alert-dialog";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";

export type DeleteDialogProps = {
  open: boolean;
  onCancel: () => void;
  onContinue: () => Promise<boolean>;
};

export const DeleteDialog: FC<DeleteDialogProps> = ({ open, onCancel, onContinue }) => {
  const { t } = useTranslation();
  const [loading, onDelete] = useLoading(() => onContinue());

  return (
    <AlertDialog open={open}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t("dialog.delete.title")}</AlertDialogTitle>
          <AlertDialogDescription>{t("dialog.delete.description")}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={() => onCancel()}>{t("action.cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={() => onDelete()} disabled={loading}>
            <Spinner loading={loading} />
            {t("action.continue")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
