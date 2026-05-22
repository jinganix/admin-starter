import i18next from "i18next";

export interface Option<T> {
  label: string;
  value: T;
}

export function enumToOptions<T extends object>(obj: T, i18nKey: string): Option<number>[] {
  return Object.values(obj)
    .filter((x) => typeof x === "string")
    .map((x) => {
      return {
        label: i18next.t(`${i18nKey || ""}.${x}`),
        value: (obj as Record<string, number>)[x],
      };
    });
}
