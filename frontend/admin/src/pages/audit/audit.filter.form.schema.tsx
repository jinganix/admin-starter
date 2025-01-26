import { FormValuesResolver } from "@helpers/search.params.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, UseFormReturn } from "react-hook-form";
import { z } from "zod";

export const formSchema = z.object({
  method: z.string().optional(),
  path: z.string().optional(),
  userId: z.string().optional(),
  username: z.string().optional(),
});

export type FormValues = z.infer<typeof formSchema>;

export const valuesResolver = new FormValuesResolver<FormValues>({
  method: ["", (x) => x],
  path: ["", (x) => x],
  userId: ["", (x) => x],
  username: ["", (x) => x],
});

export function useFilterForm<T extends FormValues>(query: T): UseFormReturn<FormValues> {
  return useForm<FormValues>({
    defaultValues: valuesResolver.resolve(),
    resolver: zodResolver(formSchema),
    values: query,
  });
}
