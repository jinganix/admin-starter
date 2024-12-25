package io.github.jinganix.admin.starter.sys.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KeyValue {

  private String key;

  private String value;
}
