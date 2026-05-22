import i18next from "i18next";
import { FC } from "react";
import { Label } from "@/components/shadcn/label.tsx";
import { Switch } from "@/components/shadcn/switch.tsx";
import { Spinner } from "@/components/ui/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";

type Props = {
  checked: boolean;
  i18nKey: string;
  onCheckedChange: (checked: boolean) => Promise<void>;
};

export const StatusSwitch: FC<Props> = ({ checked, i18nKey, onCheckedChange }) => {
  const [loading, onToggle] = useLoading(onCheckedChange, false);

  return (
    <div className="flex justify-center items-center space-x-2">
      <div className="relative">
        <Spinner loading={loading} />
        <Switch id="status" checked={checked} disabled={loading} onCheckedChange={onToggle} />
      </div>
      <Label className="whitespace-nowrap" htmlFor="status">
        {i18next.t(i18nKey)}
      </Label>
    </div>
  );
};
