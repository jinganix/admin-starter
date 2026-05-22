import { FC, ReactNode } from "react";
import { FormControl, FormItem, FormLabel, FormMessage } from "@/components/shadcn/form.tsx";

type Props = {
  label: string;
  children: ReactNode;
  controlled?: boolean;
};

export const LabeledFormItem: FC<Props> = ({ label, children, controlled = true }) => {
  return (
    <FormItem className="table-row space-x-4">
      <div className="table-cell w-[1%] pr-4 whitespace-nowrap text-right">
        <FormLabel className="text-right">{label}</FormLabel>
      </div>
      <div className="space-y-1">
        {controlled && <FormControl>{children}</FormControl>}
        {!controlled && children}
        <FormMessage />
      </div>
    </FormItem>
  );
};
