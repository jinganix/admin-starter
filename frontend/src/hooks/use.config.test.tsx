import { render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { ConfigProvider, useConfig } from "@/hooks/use.config.tsx";

const storageKey = "test-config-key";

const customDefault = {
  mode: "dark" as const,
  radius: 1,
  theme: "blue",
};

function ConfigConsumer(): React.ReactElement {
  const { config, resetConfig, setConfig } = useConfig();

  return (
    <>
      <span data-testid="mode">{config.mode}</span>
      <span data-testid="theme">{config.theme}</span>
      <span data-testid="radius">{config.radius}</span>
      <button
        type="button"
        onClick={() =>
          setConfig({
            mode: config.mode === "dark" ? "light" : "dark",
            radius: 1,
            theme: "rose",
          })
        }
      >
        toggle mode
      </button>
      <button type="button" onClick={() => resetConfig()}>
        reset
      </button>
    </>
  );
}

describe("ConfigProvider", () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.className = "";
    document.body.className = "";
    document.body.style.cssText = "";
  });

  afterEach(() => {
    localStorage.clear();
  });

  it("should apply default config when localStorage is empty", () => {
    render(
      <ConfigProvider storageKey={storageKey}>
        <ConfigConsumer />
      </ConfigProvider>,
    );

    expect(screen.getByTestId("mode")).toHaveTextContent("light");
    expect(screen.getByTestId("theme")).toHaveTextContent("zinc");
    expect(document.documentElement).toHaveClass("light");
    expect(document.body).toHaveClass("theme-zinc");
    expect(document.body.style.getPropertyValue("--radius")).toBe("0.5rem");
  });

  it("should load persisted config from localStorage when json is valid", () => {
    localStorage.setItem(
      storageKey,
      JSON.stringify({ mode: "dark", radius: 0.75, theme: "slate" }),
    );

    render(
      <ConfigProvider storageKey={storageKey}>
        <ConfigConsumer />
      </ConfigProvider>,
    );

    expect(screen.getByTestId("mode")).toHaveTextContent("dark");
    expect(screen.getByTestId("theme")).toHaveTextContent("slate");
    expect(document.documentElement).toHaveClass("dark");
    expect(document.body).toHaveClass("theme-slate");
    expect(document.body.style.getPropertyValue("--radius")).toBe("0.75rem");
  });

  it("should fall back to defaultConfig when localStorage json is invalid", () => {
    localStorage.setItem(storageKey, "not-json");

    render(
      <ConfigProvider defaultConfig={customDefault} storageKey={storageKey}>
        <ConfigConsumer />
      </ConfigProvider>,
    );

    expect(screen.getByTestId("mode")).toHaveTextContent("dark");
    expect(screen.getByTestId("theme")).toHaveTextContent("blue");
    expect(document.documentElement).toHaveClass("dark");
  });

  it("should persist config and update document classes when setConfig is called", async () => {
    render(
      <ConfigProvider storageKey={storageKey}>
        <ConfigConsumer />
      </ConfigProvider>,
    );

    await userEvent.click(screen.getByRole("button", { name: "toggle mode" }));

    expect(localStorage.getItem(storageKey)).toBe(
      JSON.stringify({ mode: "dark", radius: 1, theme: "rose" }),
    );
    expect(screen.getByTestId("mode")).toHaveTextContent("dark");
    expect(screen.getByTestId("theme")).toHaveTextContent("rose");
    expect(document.documentElement).toHaveClass("dark");
    expect(document.body).toHaveClass("theme-rose");
    expect(document.body.style.getPropertyValue("--radius")).toBe("1rem");
  });

  it("should reset config to defaults when resetConfig is called", async () => {
    localStorage.setItem(storageKey, JSON.stringify({ mode: "dark", radius: 1, theme: "rose" }));

    render(
      <ConfigProvider storageKey={storageKey}>
        <ConfigConsumer />
      </ConfigProvider>,
    );

    await userEvent.click(screen.getByRole("button", { name: "reset" }));

    expect(localStorage.getItem(storageKey)).toBe(
      JSON.stringify({ mode: "light", radius: 0.5, theme: "zinc" }),
    );
    expect(screen.getByTestId("mode")).toHaveTextContent("light");
    expect(document.documentElement).toHaveClass("light");
    expect(document.body).toHaveClass("theme-zinc");
  });
});
