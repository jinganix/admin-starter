import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from "@helpers/paging/pageable.ts";
import { IPagingPb } from "@proto/PageableProto.ts";

export class Paging {
  page = DEFAULT_PAGE;
  size = DEFAULT_PAGE_SIZE;
  pages = 0;
  total = 0;

  constructor(size = DEFAULT_PAGE_SIZE) {
    this.size = size;
  }

  static ofPb(pb: IPagingPb): Paging {
    const v = new Paging();
    v.page = pb.page;
    v.size = pb.size;
    v.pages = pb.pages;
    v.total = pb.total;
    return v;
  }
}
