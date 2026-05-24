import { FC } from "react";
import {
  IndexRouteProps,
  LayoutRouteProps,
  PathRouteProps,
  RouteProps,
  useLocation,
} from "react-router";
import { Navigate } from "react-router-dom";
import { Cond } from "@/helpers/condition/cond.types.ts";
import { useCondStore } from "@/sys/store.context.tsx";

export type CondPath = {
  cond: Cond;
  path: string;
};

export type CondRedirect = {
  cond?: Cond;
  redirects?: CondPath[];
};

export type CondRouteProps = RouteProps & CondRedirect;

export type CondIndexRouteDef = IndexRouteProps & CondRedirect;

export type CondLayoutRouteDef = LayoutRouteProps &
  CondRedirect & {
    routes: CondRouteDef[];
  };

export type CondPathRouteDef = PathRouteProps & CondRedirect;

export type CondRouteDef = CondIndexRouteDef | CondLayoutRouteDef | CondPathRouteDef;

export const CondRoute: FC<CondRouteProps> = ({ cond, redirects, element, path }) => {
  const condStore = useCondStore();
  const location = useLocation();
  if (location.pathname !== path) {
    return element;
  }
  if (condStore.satisfy(cond)) {
    return element;
  }
  for (const redirect of redirects || []) {
    if (condStore.satisfy(redirect.cond)) {
      return <Navigate to={redirect.path} replace />;
    }
  }
  return <Navigate to="/403" replace />;
};
