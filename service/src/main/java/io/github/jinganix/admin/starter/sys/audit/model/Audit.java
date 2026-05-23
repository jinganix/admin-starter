package io.github.jinganix.admin.starter.sys.audit.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Audit extends AbstractEntity {

  private Long id;

  private Long userId;

  private String method;

  private String path;

  private String params;
}
