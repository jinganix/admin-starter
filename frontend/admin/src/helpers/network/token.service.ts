import { defaultErrorHandler, HttpService } from "@helpers/network/http.service.ts";
import {
  AuthLoginRequest,
  AuthSignupRequest,
  AuthTokenRequest,
  AuthTokenResponse,
} from "@proto/SysAuthProto";
import { container, singleton } from "tsyringe";
import { v4 } from "uuid";
import { WebpbMessage } from "webpb";
import { AuthToken } from "@/helpers/network/net.types";
import { Replay } from "@/helpers/network/replay";

const TOKEN_KEY = "AUTH_TOKEN";

async function requestToken(message: WebpbMessage): Promise<AuthToken | null> {
  const res = await container
    .resolve(HttpService)
    .request(message, AuthTokenResponse, defaultErrorHandler, null);
  if (!res) {
    return null;
  }
  const token = new AuthToken();
  token.accessToken = res.accessToken;
  token.refreshToken = res.refreshToken;
  token.expiresIn = res.expiresIn;
  return token;
}

@singleton()
export class TokenService {
  private replay = new Replay<AuthToken | null>(this.readToken());

  constructor() {
    setInterval(() => void this.checkRefresh(), 10000);
  }

  async checkRefresh(): Promise<void> {
    try {
      const token = await this.replay.value();
      if (token && token.isExpired()) {
        await this.refresh(token.refreshToken);
      }
    } catch (_err) {
      // ignore errors
    }
  }

  async auth(username: string, password: string): Promise<AuthToken | null> {
    const token = await this.replay.resolve(
      () => requestToken(AuthLoginRequest.create({ password, username })),
      v4(),
    );
    if (token) {
      await this.saveToken(token);
    } else {
      this.replay = new Replay();
    }
    return token;
  }

  async signup(username: string, password: string): Promise<AuthToken | null> {
    const token = await this.replay.resolve(
      () => requestToken(AuthSignupRequest.create({ password, username })),
      v4(),
    );
    if (token) {
      await this.saveToken(token);
    } else {
      this.replay = new Replay();
    }
    return token;
  }

  async getToken(): Promise<AuthToken | null> {
    const token = await this.replay.value();
    return token && token.isExpired() ? this.refresh(token.refreshToken) : token;
  }

  async refresh(refreshToken: string): Promise<AuthToken | null> {
    const token = await this.replay.resolve(
      () => requestToken(AuthTokenRequest.create({ refreshToken })),
      "refresh",
    );
    if (token) {
      await this.saveToken(token);
    } else {
      await this.deleteToken();
    }
    return token;
  }

  readToken(): AuthToken | null {
    const data = localStorage.getItem(TOKEN_KEY);
    try {
      return data ? (Object.assign(new AuthToken(), JSON.parse(data)) as AuthToken) : null;
    } catch (e) {
      return null;
    }
  }

  private async saveToken(token: AuthToken): Promise<void> {
    localStorage.setItem(TOKEN_KEY, JSON.stringify(token));
  }

  async deleteToken(): Promise<void> {
    localStorage.removeItem(TOKEN_KEY);
    this.replay = new Replay();
  }
}
