export type LanguageDef = {
  tag: string;
  name: string;
};

export const LANGUAGE_DEFS: LanguageDef[] = [
  { name: "English (US)", tag: "en-US" },
  { name: "中文(简体)", tag: "zh-CN" },
];

export function getLanguageName(tag: string): string {
  return LANGUAGE_DEFS.find((x) => x.tag === tag)?.name || tag;
}
