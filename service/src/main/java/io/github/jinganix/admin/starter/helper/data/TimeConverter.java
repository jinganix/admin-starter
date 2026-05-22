package io.github.jinganix.admin.starter.helper.data;

import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.jooq.Converter;

public class TimeConverter implements Converter<LocalDateTime, Long> {

  private static final ZoneId ZONE_ID = UtilsService.ZONE_ID;

  @Override
  public Long from(LocalDateTime localDateTime) {
    return localDateTime == null ? null : localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
  }

  @Override
  public LocalDateTime to(Long timestamp) {
    return timestamp == null
        ? null
        : LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
  }

  @Override
  public Class<LocalDateTime> fromType() {
    return LocalDateTime.class;
  }

  @Override
  public Class<Long> toType() {
    return Long.class;
  }
}
