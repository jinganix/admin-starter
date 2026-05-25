import "reflect-metadata";
import { cleanup } from "@testing-library/react";
import { afterEach, vi } from "vitest";
import "@testing-library/jest-dom/vitest";

class ResizeObserverMock {
  observe() {}
  unobserve() {}
  disconnect() {}
}

vi.stubGlobal("ResizeObserver", ResizeObserverMock);

Object.defineProperty(window, "matchMedia", {
  writable: true,
  value: vi.fn().mockImplementation((query: string) => ({
    matches: query.includes("min-width: 1280px"),
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

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
