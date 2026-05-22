import { ErrorCode } from "@proto/ErrorCodeEnum";

export class AuthToken {
  createdAt = 0;
  accessToken = "";
  expiresIn = 0;
  refreshToken = "";

  constructor(createdAt = Date.now()) {
    this.createdAt = createdAt;
  }

  isExpired(): boolean {
    return this.createdAt + 1000 * this.expiresIn - 30000 < Date.now();
  }
}

export type NetErrorHandler = (err: NetError) => Promise<void> | void;

export class NetError extends Error {
  name = "NetError";
  code: ErrorCode;
  status?: number;

  constructor(code: ErrorCode | keyof typeof ErrorCode, status?: number, message?: string) {
    super(message ?? "");
    this.code = typeof code === "string" ? ErrorCode[code] : code;
    this.status = status;
  }
}
