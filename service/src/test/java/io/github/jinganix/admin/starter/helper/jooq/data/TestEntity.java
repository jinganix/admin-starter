package io.github.jinganix.admin.starter.helper.jooq.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestEntity {
  private Long id;

  private Long groupId;

  private String data;
}
