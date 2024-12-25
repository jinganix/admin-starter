import "reflect-metadata";
import { StrictMode } from "react";
import ReactDOM from "react-dom/client";
import { App } from "@/app.tsx";
import { ConfigProvider } from "@/hooks/use.config.tsx";
import "./index.css";
import "./themes.css";

const rootElement = document.getElementById("root")!;
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement);
  root.render(
    <StrictMode>
      <ConfigProvider>
        <App />
      </ConfigProvider>
    </StrictMode>,
  );
}
