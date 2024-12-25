import { createContext, ReactNode, useContext, useEffect, useState } from "react";

type Mode = "dark" | "light";

type Config = {
  mode: Mode;
  radius: number;
  theme: string;
};

type ConfigProviderProps = {
  children: ReactNode;
  defaultConfig?: Config;
  storageKey?: string;
};

type ConfigProviderState = {
  config: Config;
  setConfig: (config: Config) => void;
  resetConfig: () => void;
};

const DEFAULT: Config = {
  mode: "dark",
  radius: 0.5,
  theme: "zinc",
};

const ConfigContext = createContext<ConfigProviderState>({
  config: DEFAULT,
  resetConfig: () => {},
  setConfig: () => {},
});

export function ConfigProvider({
  children,
  defaultConfig = DEFAULT,
  storageKey = "admin-starter-config",
  ...props
}: ConfigProviderProps): ReactNode {
  const [config, setConfig] = useState<Config>(() => {
    try {
      return JSON.parse(localStorage.getItem(storageKey) || "");
    } catch (_) {
      return defaultConfig;
    }
  });

  useEffect(() => {
    const root = window.document.documentElement;
    root.classList.remove("light", "dark");
    root.classList.add(config.mode);
  }, [config.mode]);

  useEffect(() => {
    const body = window.document.body;
    Array.from(body.classList)
      .filter((x) => x.startsWith("theme-"))
      .forEach((x) => body.classList.remove(x));
    body.classList.add(`theme-${config.theme}`);
  }, [config.theme]);

  useEffect(() => {
    const body = window.document.body;
    body.style.setProperty("--radius", `${config.radius}rem`);
  }, [config.radius]);

  const setConfigAndPersist = (config: Config): void => {
    localStorage.setItem(storageKey, JSON.stringify(config));
    setConfig(config);
  };

  const value: ConfigProviderState = {
    config,
    resetConfig: () => setConfigAndPersist(DEFAULT),
    setConfig: setConfigAndPersist,
  };

  return (
    <ConfigContext.Provider {...props} value={value}>
      {children}
    </ConfigContext.Provider>
  );
}

export const useConfig = (): ConfigProviderState => {
  const context = useContext(ConfigContext);
  if (context === undefined) {
    throw new Error("useConfig must be used within a ConfigProvider");
  }
  return context;
};
