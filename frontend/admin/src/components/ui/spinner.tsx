import { Loader2Icon } from "lucide-react";
import { FC, HTMLAttributes } from "react";
import { cn } from "@/helpers/lib/cn";

type Props = {
  loading: boolean;
  size?: number;
  className?: string;
} & HTMLAttributes<HTMLDivElement>;

export const Spinner: FC<Props> = ({ loading, size, className, ...props }) => {
  return (
    <>
      {loading && (
        <div
          className={cn("absolute w-full h-full flex items-center justify-center z-50", className)}
          {...props}
        >
          <Loader2Icon {...(size ? { size } : {})} className="relative animate-spin text-primary" />
        </div>
      )}
    </>
  );
};
