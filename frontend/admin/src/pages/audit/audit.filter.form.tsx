import { XIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button.tsx";
import { Form, FormControl, FormField, FormItem } from "@/components/shadcn/form.tsx";
import { Input } from "@/components/shadcn/input.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { TableFilters } from "@/components/table/table.filters.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import { useFilterForm, valuesResolver } from "@/pages/audit/audit.filter.form.schema.tsx";
import { AuditQuery } from "@/sys/audit/audit.types.ts";

export const AuditFilterForm: FC = () => {
  const { t } = useTranslation();

  const { query, setQuery } = useTableData<AuditQuery>();
  const form = useFilterForm(query);

  const [resetting, reset] = useLoading(async () => {
    form.reset(valuesResolver.resolve());
    await setQuery({});
  }, false);

  const [submitting, submit] = useLoading(async () => {
    const values = form.getValues();
    await setQuery(values);
  }, false);

  return (
    <TableFilters>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(submit)} className="flex flex-col xl:flex-row gap-4">
          <FormField
            control={form.control}
            name="userId"
            render={({ field }) => (
              <FormItem className="w-64 xl:w-auto">
                <FormControl>
                  <div className="relative w-full max-w-sm">
                    <Input
                      className="h-8 placeholder:text-sm"
                      placeholder={t("audit.filter.userId")}
                      {...field}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
                      onClick={() => form.setValue("userId", "")}
                    >
                      <XIcon className="h-4 w-4" />
                    </Button>
                  </div>
                </FormControl>
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem className="w-64 xl:w-auto">
                <FormControl>
                  <div className="relative w-full max-w-sm">
                    <Input
                      className="h-8 placeholder:text-sm"
                      placeholder={t("audit.filter.username")}
                      {...field}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
                      onClick={() => form.setValue("username", "")}
                    >
                      <XIcon className="h-4 w-4" />
                    </Button>
                  </div>
                </FormControl>
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="path"
            render={({ field }) => (
              <FormItem className="w-64 xl:w-auto">
                <FormControl>
                  <div className="relative w-full max-w-sm">
                    <Input
                      className="h-8 placeholder:text-sm"
                      placeholder={t("audit.filter.path")}
                      {...field}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
                      onClick={() => form.setValue("path", "")}
                    >
                      <XIcon className="h-4 w-4" />
                    </Button>
                  </div>
                </FormControl>
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="method"
            render={({ field }) => (
              <FormItem className="w-64 xl:w-auto">
                <FormControl>
                  <div className="relative w-full max-w-sm">
                    <Input
                      className="h-8 placeholder:text-sm"
                      placeholder={t("audit.filter.method")}
                      {...field}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
                      onClick={() => form.setValue("method", "")}
                    >
                      <XIcon className="h-4 w-4" />
                    </Button>
                  </div>
                </FormControl>
              </FormItem>
            )}
          />
          <div className="flex justify-end xl:justify-between gap-4">
            <Button className="h-8" type="button" onClick={reset}>
              <Spinner loading={resetting} />
              {t("action.reset")}
            </Button>
            <Button disabled={submitting} className="h-8" type="submit">
              <Spinner loading={submitting} />
              {t("action.search")}
            </Button>
          </div>
        </form>
      </Form>
    </TableFilters>
  );
};
