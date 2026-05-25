import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { LanguageSwitch } from "@/components/layout/language.switch.tsx";

const changeLanguage = vi.fn().mockResolvedValue(undefined);

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    i18n: {
      changeLanguage,
      language: "en-US",
    },
  }),
}));

describe("<LanguageSwitch />", () => {
  afterEach(() => vi.clearAllMocks());

  it("should render current language when mounted", () => {
    render(<LanguageSwitch />);

    expect(screen.getByText("English (US)")).toBeInTheDocument();
  });

  it("should change language when user selects another option", async () => {
    render(<LanguageSwitch />);

    await userEvent.click(screen.getByRole("button"));
    await userEvent.click(screen.getByText("中文(简体)"));

    expect(changeLanguage).toHaveBeenCalledWith("zh-CN");
  });
});
