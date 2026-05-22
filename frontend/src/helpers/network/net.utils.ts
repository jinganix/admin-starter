import { WebpbMessage } from "webpb";
import { NetError } from "@/helpers/network/net.types";

export function tryJsonParse<T>(str: string): T | null {
  try {
    return JSON.parse(str);
  } catch (_) {
    return null;
  }
}

export function fromAlias<M extends WebpbMessage>(data: object, messageType?: { prototype: M }): M {
  const fromAlias =
    messageType && (messageType as unknown as { fromAlias: (data: unknown) => M }).fromAlias;
  return fromAlias ? fromAlias(data) : (data as M);
}

export function isNetError(err: unknown): boolean {
  return !!err && typeof err === "object" && err instanceof NetError;
}
