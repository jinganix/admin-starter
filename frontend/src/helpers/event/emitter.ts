import { ErrorCode } from "@proto/ErrorCodeEnum";
import { createNanoEvents } from "nanoevents";

export interface Emitter {
  error: (code: ErrorCode, status?: number) => void;
}

export const emitter = createNanoEvents<Emitter>();
