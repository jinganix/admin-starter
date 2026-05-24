import { CircleCheck, Info, LoaderCircle, OctagonX, TriangleAlert } from "lucide-react";
import { useTheme } from "next-themes";
import { Toaster as Sonner } from "sonner";

type ToasterProps = React.ComponentProps<typeof Sonner>;

const Toaster = ({ ...props }: ToasterProps): React.JSX.Element => {
  const { theme = "system" } = useTheme();

  return (
    <Sonner
      theme={theme as ToasterProps["theme"]}
      className="toaster group"
      icons={{
        error: <OctagonX className="h-4 w-4" />,
        info: <Info className="h-4 w-4" />,
        loading: <LoaderCircle className="h-4 w-4 animate-spin" />,
        success: <CircleCheck className="h-4 w-4" />,
        warning: <TriangleAlert className="h-4 w-4" />,
      }}
      toastOptions={{
        classNames: {
          actionButton: "group-[.toast]:bg-primary group-[.toast]:text-primary-foreground",
          cancelButton: "group-[.toast]:bg-muted group-[.toast]:text-muted-foreground",
          description: "group-[.toast]:text-muted-foreground",
          toast:
            "group toast group-[.toaster]:bg-background group-[.toaster]:text-foreground group-[.toaster]:border-border group-[.toaster]:shadow-lg",
        },
      }}
      {...props}
    />
  );
};

export { Toaster };
