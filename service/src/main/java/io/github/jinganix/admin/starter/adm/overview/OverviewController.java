package io.github.jinganix.admin.starter.adm.overview;

import io.github.jinganix.admin.starter.adm.overview.handler.OverviewRetrieveHandler;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListRequest;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListResponse;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OverviewController {

  private final OverviewRetrieveHandler overviewRetrieveHandler;

  @PreAuthorize(
      "hasAuthority(T(io.github.jinganix.admin.starter.sys.permission.Authority).ADM_OVERVIEW_LIST)")
  @WebpbRequestMapping(message = OverviewListRequest.class)
  public OverviewListResponse list() {
    return overviewRetrieveHandler.handle();
  }
}
