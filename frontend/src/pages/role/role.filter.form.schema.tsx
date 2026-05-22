import { FormValuesResolver } from "@helpers/search.params.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { RoleStatus } from "@proto/SysRoleProto.ts";
import { useForm, UseFormReturn } from "react-hook-form";
import { z } from "zod";

export const formSchema = z.object({
  name: z.string().optional(),
  status: z.nativeEnum(RoleStatus).optional().nullable(),
});

export type FormValues = z.infer<typeof formSchema>;

export const valuesResolver = new FormValuesResolver<FormValues>({
  name: ["", (x) => x],
  status: [null, (x) => Number(x)],
});

export function useFilterForm<T extends FormValues>(query: T): UseFormReturn<FormValues> {
  return useForm<FormValues>({
    defaultValues: valuesResolver.resolve(),
    resolver: zodResolver(formSchema),
    values: query,
  });
}
