import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { FC, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "sonner";
import { Toaster as SonnerToaster } from "@/components/shadcn/sonner.tsx";
import { useConfig } from "@/hooks/use.config.tsx";

export const Toaster: FC = () => {
  const { t } = useTranslation();
  const { config } = useConfig();

  useEffect(
    () =>
      emitter.on("error", (code, status) => {
        if (code === ErrorCode.OK) {
          toast.success(`${t(`error.code.${code}_${ErrorCode[code]}`)}`);
        } else if (code === ErrorCode.ERROR && status) {
          toast.error(`${t(`error.status.${status}`)}`);
        } else {
          toast.error(`${t(`error.code.${code}_${ErrorCode[code]}`)}`);
        }
      }),
    [],
  );

  return (
    <SonnerToaster
      theme={config.mode}
      richColors
      closeButton
      className="z-100 pointer-events-auto"
    />
  );
};
