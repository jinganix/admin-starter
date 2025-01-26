import { enumToOptions } from "@helpers/option.ts";
import {
  PermissionStatus,
  PermissionType,
  PermissionTypeValues,
} from "@proto/SysPermissionProto.ts";
import { XIcon } from "lucide-react";
import { FC } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/shadcn/button.tsx";
import { Form, FormControl, FormField, FormItem } from "@/components/shadcn/form.tsx";
import { Input } from "@/components/shadcn/input.tsx";
import { OptionSelector } from "@/components/table/option.selector.tsx";
import { useTableData } from "@/components/table/table.data.context.tsx";
import { TableFilters } from "@/components/table/table.filters.tsx";
import { FacetedFilter } from "@/components/utils/faceted.filter.tsx";
import { Spinner } from "@/components/utils/spinner.tsx";
import { useLoading } from "@/hooks/use.loading.ts";
import {
  useFilterForm,
  valuesResolver,
} from "@/pages/permission/permission.filter.form.schema.tsx";
import { PermissionQuery } from "@/sys/permission/permission.types.ts";

export const PermissionFilterForm: FC = () => {
  const { t } = useTranslation();

  const { query, setQuery } = useTableData<PermissionQuery>();
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
            name="code"
            render={({ field }) => (
              <FormItem className="w-64 xl:w-auto">
                <FormControl>
                  <div className="relative w-full max-w-sm">
                    <Input
                      autoFocus={false}
                      className="h-8 placeholder:text-sm"
                      placeholder={t("permission.filter.code.placeholder")}
                      {...field}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
                      onClick={() => form.setValue("code", "")}
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
            name="types"
            render={() => (
              <FormItem>
                <FacetedFilter
                  className="h-8"
                  title={t("permission.filter.type")}
                  options={PermissionTypeValues.map((x) => ({
                    label: t(`permission.type.${PermissionType[x]}`),
                    value: x,
                  }))}
                  setSelected={(values) => form.setValue("types", values)}
                  selected={form.getValues("types")}
                />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="status"
            render={() => (
              <FormItem>
                <OptionSelector
                  options={enumToOptions(PermissionStatus, "permission.status")}
                  placeholder={t("permission.status.unselected")}
                  className="h-8"
                  selected={form.getValues("status") as number}
                  setSelected={(value?: number) => form.setValue("status", value)}
                />
              </FormItem>
            )}
          />

          <div className="flex justify-end xl:justify-between gap-4">
            <Button disabled={resetting} className="h-8" type="button" onClick={reset}>
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
