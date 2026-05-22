package io.github.jinganix.admin.starter.helper.jackson;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class CollectionLongToStringSerializer extends ValueSerializer<Collection<Long>> {

  @Override
  public void serialize(
      Collection<Long> value, JsonGenerator gen, SerializationContext serializers) {
    List<String> stringList = value.stream().map(String::valueOf).collect(Collectors.toList());
    gen.writePOJO(stringList);
  }
}
