import { environment } from "@helpers/environment.ts";
import { emitter } from "@helpers/event/emitter.ts";
import axios from "axios";
import { container, singleton } from "tsyringe";
import { WebpbMessage, WebpbMeta } from "webpb";
import { ErrorNormalizer } from "@/helpers/network/error.normalizer";
import { NetError, NetErrorHandler } from "@/helpers/network/net.types";
import { fromAlias, isNetError } from "@/helpers/network/net.utils";
import { TokenService } from "@/helpers/network/token.service";
import { urlJoin } from "@/helpers/network/url.join";

axios.defaults.headers.post["Content-Type"] = "application/json;charset=utf-8";

function formatUrl(baseUrl: string, meta: WebpbMeta): string {
  const context = meta.context || "";
  const path = meta.path || "";
  return urlJoin(baseUrl, context, path);
}

export const defaultErrorHandler: NetErrorHandler = (err) => {
  emitter.emit("error", err.code, err.status);
};

@singleton()
export class HttpService {
  private errorNormalizer = new ErrorNormalizer();

  async request<R extends WebpbMessage>(
    message: WebpbMessage,
    responseType?: { prototype: R },
    errorHandler: NetErrorHandler | null = defaultErrorHandler,
    tokenService: TokenService | null = container.resolve(TokenService),
  ): Promise<R | null> {
    try {
      const token = tokenService && (await tokenService.getToken());
      const headers = {
        ...(token ? { authorization: `Bearer ${token.accessToken}` } : {}),
        "content-type": "application/json;charset=utf-8",
      };
      console.log(message);
      const meta = message.webpbMeta();
      const url = formatUrl(environment.apiHost, meta);
      const res = await axios.request({
        data: message.toWebpbAlias() as Record<string, unknown>,
        headers,
        method: meta.method,
        responseType: "json",
        timeout: 10000,
        url,
      });
      const response = fromAlias(res.data as Record<string, unknown>, responseType);
      console.log(response);
      return response;
    } catch (err) {
      await this.handleError(errorHandler, err);
    }
    return null;
  }

  private async handleError(errorHandler: NetErrorHandler | null, err: unknown): Promise<void> {
    const netErr = this.errorNormalizer.normalize(err);
    if (errorHandler && isNetError(netErr)) {
      await errorHandler(netErr as NetError);
    } else {
      throw netErr;
    }
  }
}
