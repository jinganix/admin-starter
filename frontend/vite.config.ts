import tailwindcss from "@tailwindcss/vite";
import legacy from "@vitejs/plugin-legacy";
import react from "@vitejs/plugin-react";
import { resolve } from "path";
import checker from "vite-plugin-checker";
import { defineConfig } from "vitest/config";

// https://vite.dev/config/
export default defineConfig({
  build: {
    chunkSizeWarningLimit: 2000,
    rolldownOptions: {
      checks: {
        invalidAnnotation: false,
        pluginTimings: false,
      },
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: "modern",
      },
    },
  },
  envDir: "./env",
  plugins: [
    checker({
      typescript: { buildMode: false, tsconfigPath: "./tsconfig.app.json" },
    }),
    react(),
    legacy({ targets: ["defaults", "not IE 11"] }),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
      "@proto": resolve(__dirname, "build/generated/sources/proto/main/ts"),
    },
    tsconfigPaths: true,
  },
  server: {
    allowedHosts: true,
    host: "0.0.0.0",
    port: 5174,
  },
  test: {
    coverage: {
      exclude: ["src/components/shadcn/**", "src/index.tsx"],
      include: ["src/**"],
    },
    environment: "jsdom",
    globals: true,
    resolveSnapshotPath: (testPath, snapExtension) => {
      return (testPath + snapExtension).replace(
        "frontend/src/",
        "frontend/tests/__snapshots__/",
      );
    },
    setupFiles: "./tests/setup.ts",
  },
});
