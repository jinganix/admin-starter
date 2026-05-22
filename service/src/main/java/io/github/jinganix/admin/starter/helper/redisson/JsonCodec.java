package io.github.jinganix.admin.starter.helper.redisson;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class JsonCodec extends BaseCodec {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final Decoder<Object> decoder;

  private final Encoder encoder =
      in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try {
          ByteBufOutputStream os = new ByteBufOutputStream(out);
          objectMapper.writeValue((OutputStream) os, in);
          return os.buffer();
        } catch (Exception e) {
          out.release();
          throw e;
        }
      };

  public JsonCodec(TypeReference<?> valueTypeRef) {
    this.decoder =
        (buf, state) ->
            objectMapper.readValue((InputStream) new ByteBufInputStream(buf), valueTypeRef);
  }

  public JsonCodec(Class<?> valueType) {
    this.decoder =
        (buf, state) ->
            objectMapper.readValue((InputStream) new ByteBufInputStream(buf), valueType);
  }

  @Override
  public Decoder<Object> getValueDecoder() {
    return decoder;
  }

  @Override
  public Encoder getValueEncoder() {
    return encoder;
  }
}
