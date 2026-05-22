import "reflect-metadata";
import { cleanup } from "@testing-library/react";
import { afterEach, vi } from "vitest";
import "@testing-library/jest-dom/vitest";

vi.mock("react-i18next", () => ({
  initReactI18next: {
    init: () => {},
    type: "3rdParty",
  },
  useTranslation: () => {
    return {
      i18n: {
        changeLanguage: () => new Promise(() => {}),
      },

      t: (i18nKey: string) => i18nKey,
    };
  },
}));

afterEach(() => {
  cleanup();
});
