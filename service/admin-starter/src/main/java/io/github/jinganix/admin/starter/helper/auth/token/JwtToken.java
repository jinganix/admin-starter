package io.github.jinganix.admin.starter.helper.auth.token;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class JwtToken {

  public static final JwtToken INVALID_TOKEN = new JwtToken(null, null, Collections.emptyList());

  private final Long userId;

  private final String uuid;

  private final List<String> authorities;

  public boolean isValid() {
    return uuid != null;
  }
}
