import i18next from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";
import en_US from "./en-US/translation.json";
import zh_CN from "./zh-CN/translation.json";

void i18next
  .use(initReactI18next)
  .use(LanguageDetector)
  .init({
    debug: false,
    fallbackLng: "en-US",
    resources: {
      "en-US": { translation: en_US },
      "zh-CN": { translation: zh_CN },
    },
  });
