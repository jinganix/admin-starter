import { UserStatus } from "@proto/SysUserProto.ts";

export interface User {
  id: string;
  username: string;
  nickname: string;
  status: UserStatus;
  roleIds?: string[];
  createdAt: number;
}

export type UserQuery = {
  username?: string;
  status?: UserStatus;
};
