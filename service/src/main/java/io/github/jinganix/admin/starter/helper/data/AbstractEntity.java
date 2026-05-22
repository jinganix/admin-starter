package io.github.jinganix.admin.starter.helper.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractEntity implements Serializable {

  private Long createdAt;

  private Long updatedAt;
}
