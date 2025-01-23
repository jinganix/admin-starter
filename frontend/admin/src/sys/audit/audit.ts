import { IAuditDetailsPb, IAuditPb } from "@proto/SysAuditProto.ts";
import { makeAutoObservable } from "mobx";

export class Audit {
  id = "";
  username = "";
  method = "";
  path = "";
  params = "";
  createdAt = 0;

  constructor() {
    makeAutoObservable(this);
  }

  static ofPb(pb: IAuditPb): Audit {
    const v = new Audit();
    v.id = pb.id;
    v.username = pb.username;
    v.method = pb.method;
    v.path = pb.path;
    v.createdAt = pb.createdAt;
    return v;
  }

  static ofDetailsPb(pb: IAuditDetailsPb): Audit {
    const v = this.ofPb(pb);
    v.params = pb.params;
    return v;
  }
}
