package io.github.jinganix.admin.starter.adm.overview.model;

import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "biz_overview")
public class Overview extends AbstractEntity {

  @Id private Long id;

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
