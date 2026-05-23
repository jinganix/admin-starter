package io.github.jinganix.admin.starter.adm.overview;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewPb;
import io.github.jinganix.admin.starter.schema.tables.records.AdminOverviewRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OverviewMapper {

  OverviewPb overviewPb(Overview overview);

  Overview toEntity(AdminOverviewRecord record);

  AdminOverviewRecord toRecord(Overview entity);
}
