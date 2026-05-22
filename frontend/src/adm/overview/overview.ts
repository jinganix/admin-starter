import { IOverviewPb } from "@proto/AdmOverviewProto.ts";
import { makeAutoObservable } from "mobx";

export class Overview {
  month = "";
  apiGet = 0;
  apiPost = 0;
  userCreated = 0;
  userDeleted = 0;
  roleCreated = 0;
  roleDeleted = 0;
  permissionCreated = 0;
  permissionDeleted = 0;

  constructor() {
    makeAutoObservable(this);
  }

  static ofPb(pb: IOverviewPb): Overview {
    const v = new Overview();
    v.month = pb.month;
    v.apiGet = pb.apiGet;
    v.apiPost = pb.apiPost;
    v.userCreated = pb.userCreated;
    v.userDeleted = pb.userDeleted;
    v.roleCreated = pb.roleCreated;
    v.roleDeleted = pb.roleDeleted;
    v.permissionCreated = pb.permissionCreated;
    v.permissionDeleted = pb.permissionDeleted;
    return v;
  }
}
