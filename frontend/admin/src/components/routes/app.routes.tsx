import { emitter } from "@helpers/event/emitter.ts";
import { ErrorCode } from "@proto/ErrorCodeEnum.ts";
import { ReactNode, useEffect } from "react";
import { Route, Routes } from "react-router";
import { useNavigate } from "react-router-dom";
import { CondRoute, CondRouteDef } from "@/components/condition/cond.route.tsx";
import { ROUTES } from "@/components/routes/routes.tsx";

function toRoutes(routes: CondRouteDef[] = []): ReactNode[] {
  return routes.map((props) => {
    if (!props.cond) {
      if ("routes" in props) {
        return <Route {...props}>{...toRoutes(props.routes)}</Route>;
      }
      return <Route {...props} />;
    }
    if ("routes" in props) {
      return (
        <Route {...props} element={<CondRoute {...props} />}>
          {...toRoutes(props.routes)}
        </Route>
      );
    } else {
      return <Route {...props} element={<CondRoute {...props} />} />;
    }
  });
}

export function AppRoutes(): ReactNode {
  const navigate = useNavigate();
  useEffect(
    () =>
      emitter.on("error", (code) => {
        switch (code) {
          case ErrorCode.ACCESS_DENIED:
          case ErrorCode.USER_IS_INACTIVE:
          case ErrorCode.PERMISSION_DENIED:
            navigate("/403");
        }
      }),
    [],
  );
  return <Routes>{...toRoutes(ROUTES)}</Routes>;
}
