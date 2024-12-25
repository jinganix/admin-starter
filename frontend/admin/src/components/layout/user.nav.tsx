import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { LogOutIcon, RefreshCwIcon, SettingsIcon } from "lucide-react";
import { observer } from "mobx-react-lite";
import { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router";
import { Avatar, AvatarFallback } from "@/components/shadcn/avatar";
import { Button } from "@/components/shadcn/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { authStore } from "@/sys/auth/auth.store.ts";
import { logout } from "@/sys/user/user.utils.ts";

export const UserNav: FC = observer(() => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [loadingUser, onLoadUser] = useLoading(async () => {
    if (await authStore.loadCurrent()) {
      setOpen(false);
      emitter.emit("error", ErrorCode.OK);
    }
  }, false);

  const checkSetOpen = (open: boolean) => void ((open || !loadingUser) && setOpen(open));

  return (
    <DropdownMenu open={open} onOpenChange={(x) => checkSetOpen(x)}>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative h-8 w-8 rounded-full">
          <Avatar className="h-8 w-8 border border-zinc-400">
            <AvatarFallback>{authStore.nickname.toUpperCase().substring(0, 2)}</AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-56" align="end" forceMount>
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col space-y-1">
            <p className="text-sm font-medium leading-none">{authStore.nickname}</p>
            <p className="text-xs leading-none text-muted-foreground">{authStore.username}</p>
          </div>
        </DropdownMenuLabel>

        <DropdownMenuSeparator />

        <Link to="/settings">
          <DropdownMenuItem>
            {t("user.nav.settings")}
            <DropdownMenuShortcut>
              <SettingsIcon size={16} />
            </DropdownMenuShortcut>
          </DropdownMenuItem>
        </Link>

        <DropdownMenuItem disabled={loadingUser} onClick={() => onLoadUser()}>
          <Spinner loading={loadingUser} />
          {t("user.nav.refresh")}
          <DropdownMenuShortcut>
            <RefreshCwIcon size={16} />
          </DropdownMenuShortcut>
        </DropdownMenuItem>

        <DropdownMenuSeparator />

        <DropdownMenuItem onClick={() => logout()}>
          {t("user.nav.signOut")}
          <DropdownMenuShortcut>
            <LogOutIcon size={16} />
          </DropdownMenuShortcut>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
});
