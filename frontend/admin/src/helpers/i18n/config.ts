import i18next from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";
import en from "./en/translation.json";
import zh from "./zh/translation.json";

void i18next
  .use(initReactI18next)
  .use(LanguageDetector)
  .init({
    debug: false,
    fallbackLng: "en",
    resources: {
      en: { translation: en },
      zh: { translation: zh },
    },
  });
