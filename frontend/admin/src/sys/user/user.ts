import { IUserDetailsPb, IUserPb, UserStatus } from "@proto/SysUserProto.ts";
import { makeAutoObservable } from "mobx";

export class User {
  id = "";
  username = "";
  nickname = "";
  status = UserStatus.ACTIVE;
  roleIds: string[] = [];
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  static ofPb(pb: IUserPb): User {
    const v = new User();
    v.id = pb.id;
    v.username = pb.username;
    v.nickname = pb.nickname;
    v.status = pb.status;
    v.createdAt = pb.createdAt;
    return v;
  }

  static ofDetailsPb(pb: IUserDetailsPb): User {
    const v = User.ofPb(pb);
    v.roleIds = pb.roleIds;
    return v;
  }

  setStatus(status: UserStatus): User {
    this.status = status;
    return this;
  }
}
