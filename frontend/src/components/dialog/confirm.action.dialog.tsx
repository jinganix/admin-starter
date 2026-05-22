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
import { Spinner } from "@/components/ui/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";

export type ConfirmDialogProps = {
  titleKey?: string;
  descriptionKey?: string;
  open: boolean;
  onCancel: () => void;
  onContinue: () => Promise<boolean>;
};

export const ConfirmActionDialog: FC<ConfirmDialogProps> = ({
  titleKey = "dialog.confirm.title",
  descriptionKey = "dialog.confirm.description",
  open,
  onCancel,
  onContinue,
}) => {
  const { t } = useTranslation();
  const [loading, onConfirm] = useLoading(() => onContinue());

  return (
    <AlertDialog open={open}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{t(titleKey)}</AlertDialogTitle>
          <AlertDialogDescription>{t(descriptionKey)}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel onClick={() => onCancel()}>{t("action.cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={() => onConfirm()} disabled={loading}>
            <Spinner loading={loading} />
            {t("action.continue")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
