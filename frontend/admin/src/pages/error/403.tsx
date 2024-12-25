import { FC } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/shadcn/button";
import { logout } from "@/sys/user/user.utils.ts";

export const Error403: FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <div className="h-svh">
      <div className="m-auto flex h-full w-full flex-col items-center justify-center gap-2">
        <h1 className="text-[7rem] font-bold leading-tight">403</h1>
        <span className="font-medium">{t("error.page.403.reason")}</span>
        <p className="text-center text-muted-foreground w-72">{t("error.page.403.description")}</p>
        <div className="mt-6 flex gap-4">
          <Button variant="outline" onClick={() => navigate(-1)}>
            {t("error.page.goBack")}
          </Button>
          <Button onClick={() => logout()}>{t("error.page.signOut")}</Button>
        </div>
      </div>
    </div>
  );
};
