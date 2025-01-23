import { FC, ReactNode } from "react";
import { AlignedLabel } from "@/components/form/aligned.label.tsx";
import { FormControl, FormItem, FormMessage } from "@/components/shadcn/form.tsx";

type Props = {
  label: string;
  children: ReactNode;
  controlled?: boolean;
};

export const LabeledFormItem: FC<Props> = ({ label, children, controlled = true }) => {
  return (
    <FormItem className="space-y-1">
      <div className="flex items-center space-x-4">
        <AlignedLabel>{label}</AlignedLabel>
        {controlled && (
          <FormControl>
            <div className="flex-1">{children}</div>
          </FormControl>
        )}
        {!controlled && <div className="flex-1">{children}</div>}
      </div>
      <div className="flex items-center space-x-4">
        <AlignedLabel />
        <FormMessage />
      </div>
    </FormItem>
  );
};
