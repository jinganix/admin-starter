package io.github.jinganix.admin.starter.adm.overview.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Overview extends AbstractEntity {

  private Long id;

  private LocalDate month;

  private long apiGet;

  private long apiPost;

  private long userCreated;

  private long userDeleted;

  private long roleCreated;

  private long roleDeleted;

  private long permissionCreated;

  private long permissionDeleted;
}
