package io.github.jinganix.admin.starter.helper.jackson.enumeration;

import io.github.jinganix.webpb.runtime.enumeration.Enumeration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class EnumerationSerializer extends StdSerializer<Enumeration<?>> {

  public EnumerationSerializer() {
    this(null);
  }

  public EnumerationSerializer(Class<Enumeration<?>> t) {
    super(t);
  }

  @Override
  public void serialize(Enumeration value, JsonGenerator gen, SerializationContext context) {
    if (value.getValue() instanceof Integer) {
      gen.writeNumber((Integer) value.getValue());
    } else if (value.getValue() instanceof String) {
      gen.writeString((String) value.getValue());
    } else {
      throw new IllegalArgumentException();
    }
  }
}
