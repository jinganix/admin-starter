import { TokenService } from "@helpers/network/token.service.ts";
import { container } from "tsyringe";
import { authStore } from "@/sys/auth/auth.store.ts";

export async function logout(): Promise<void> {
  await container.resolve(TokenService).deleteToken();
  authStore.dispose();
}
