import react from "@vitejs/plugin-react";
import { resolve } from "path";
import tailwindcss from "tailwindcss";
import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig } from "vitest/config";

// https://vite.dev/config/
export default defineConfig({
  css: {
    postcss: {
      plugins: [tailwindcss()],
    },
    preprocessorOptions: {
      scss: {
        api: "modern",
      },
    },
  },
  envDir: "./env",
  plugins: [react(), tsconfigPaths()],
  resolve: {
    alias: {
      "@proto": resolve(__dirname, "build/generated/source/proto/main/ts"),
    },
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
        "frontend/admin/src/",
        "frontend/admin/tests/__snapshots__/",
      );
    },
    setupFiles: "./tests/setup.ts",
  },
});
