package io.github.jinganix.admin.starter.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jinganix.admin.starter.helper.data.LongToDateConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@MappedSuperclass
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractEntity implements Serializable {

  @Convert(converter = LongToDateConverter.class)
  private Long createdAt;

  @Convert(converter = LongToDateConverter.class)
  private Long updatedAt;
}
