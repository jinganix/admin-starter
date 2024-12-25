package io.github.jinganix.admin.starter.adm.overview;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewPb;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OverviewMapper {

  OverviewPb overviewPb(Overview overview);
}
