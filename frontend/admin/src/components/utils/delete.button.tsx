import { Trash2Icon } from "lucide-react";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { DeleteDialog } from "@/components/dialog/delete.dialog.tsx";
import { Button, ButtonProps } from "@/components/shadcn/button.tsx";

type Props = {
  onDelete: () => Promise<boolean>;
} & ButtonProps;

export const DeleteButton: FC<Props> = ({ onDelete, ...props }) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const onContinue = async (): Promise<boolean> => {
    if (await onDelete()) {
      setOpen(false);
      return true;
    }
    return false;
  };

  return (
    <>
      <Button
        variant="outline"
        size="sm"
        className="ml-auto h-8 !text-red-500 !border-red-500"
        onClick={() => setOpen(true)}
        {...props}
      >
        <Trash2Icon />
        <span className="hidden lg:block">{t("action.delete")}</span>
      </Button>

      <DeleteDialog open={open} onCancel={() => setOpen(false)} onContinue={onContinue} />
    </>
  );
};
