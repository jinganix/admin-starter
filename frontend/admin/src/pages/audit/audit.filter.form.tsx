import { FormValuesResolver } from "@helpers/search.params.ts";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, UseFormReturn } from "react-hook-form";
import { z } from "zod";

export const formSchema = z.object({
  method: z.string().optional(),
  path: z.string().optional(),
  username: z.string().optional(),
});

export type FormValues = z.infer<typeof formSchema>;

export const valuesResolver = new FormValuesResolver<FormValues>({
  method: ["", (x) => x],
  path: ["", (x) => x],
  username: ["", (x) => x],
});

export const useFilterForm = (params: URLSearchParams): UseFormReturn<FormValues> => {
  return useForm<FormValues>({
    defaultValues: valuesResolver.resolve(),
    resolver: zodResolver(formSchema),
    values: valuesResolver.resolve(params),
  });
};
