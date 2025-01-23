package io.github.jinganix.admin.starter.helper.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionLongToStringSerializer extends JsonSerializer<Collection<Long>> {

  @Override
  public void serialize(Collection<Long> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    List<String> stringList = value.stream().map(String::valueOf).collect(Collectors.toList());
    gen.writeObject(stringList);
  }
}
