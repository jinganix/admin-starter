import { ErrorCode } from "@proto/ErrorCodeEnum";
import { ErrorMessage } from "@proto/ErrorProto";
import axios, { AxiosError } from "axios";
import { NetError } from "@/helpers/network/net.types";
import { fromAlias, isNetError, tryJsonParse } from "@/helpers/network/net.utils";

export class ErrorNormalizer {
  private extractMessage(err: unknown): string | null {
    if (!err) {
      return null;
    }
    let message;
    if (typeof err === "object") {
      if ("message" in err && typeof err.message === "string") {
        message = err.message;
      }
    } else if (typeof err === "string") {
      message = err;
    }
    return !message ? null : message;
  }

  private normalizeAxiosError(err: AxiosError): NetError | unknown {
    if (err.code === AxiosError.ERR_NETWORK) {
      return new NetError(ErrorCode.ERROR_NETWORK);
    } else if (err.code === AxiosError.ETIMEDOUT) {
      return new NetError(ErrorCode.REQUEST_TIMEOUT);
    } else {
      return this.handleErrorMessage(err.response?.data || null, err.status, err.message);
    }
  }

  private handleErrorMessage(
    data: object | null,
    status?: number,
    message?: string,
  ): NetError | null {
    if (data != null && typeof data === "object") {
      const errorMessage = data && fromAlias(data, ErrorMessage);
      if (errorMessage?.code) {
        return new NetError(errorMessage.code, status, message);
      }
    }
    return new NetError(ErrorCode.ERROR, status, message);
  }

  normalize(err: unknown): NetError | unknown {
    if (!err) {
      return err;
    }
    if (isNetError(err)) {
      return err;
    }
    if (axios.isAxiosError(err)) {
      return this.normalizeAxiosError(err);
    }
    const message = this.extractMessage(err);
    if (message === "timeout") {
      return new NetError(ErrorCode.REQUEST_TIMEOUT);
    } else if (message === "Access Denied") {
      return new NetError(ErrorCode.ACCESS_DENIED);
    } else if (message) {
      const data: object | null = tryJsonParse(message);
      return this.handleErrorMessage(data);
    }
    return new NetError(ErrorCode.ERROR);
  }
}
