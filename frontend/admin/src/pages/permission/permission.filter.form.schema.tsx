import { FormValuesResolver } from "@helpers/search.params.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { PermissionStatus, PermissionType } from "@proto/SysPermissionProto.ts";
import { useForm, UseFormReturn } from "react-hook-form";
import { z } from "zod";

export const formSchema = z.object({
  code: z.string().optional(),
  status: z.nativeEnum(PermissionStatus).optional(),
  types: z.array(z.nativeEnum(PermissionType)).optional(),
});

export type FormValues = z.infer<typeof formSchema>;

export const valuesResolver = new FormValuesResolver<FormValues>({
  code: ["", (x) => x],
  status: [undefined, (x) => Number(x)],
  types: [undefined, (x) => x.split(",").map((v) => Number(v))],
});

export function useFilterForm<T extends FormValues>(query: T): UseFormReturn<FormValues> {
  return useForm<FormValues>({
    defaultValues: valuesResolver.resolve(),
    resolver: zodResolver(formSchema),
    values: query,
  });
}
