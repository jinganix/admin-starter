package io.github.jinganix.admin.starter.sys.role.handler;

import io.github.jinganix.admin.starter.proto.lib.option.OptionStringPb;
import io.github.jinganix.admin.starter.proto.sys.role.RoleOptionsResponse;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RoleStatus;
import io.github.jinganix.admin.starter.sys.role.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleOptionsHandler {

  private final RoleRepository roleRepository;

  public RoleOptionsResponse handle() {
    List<Role> roles = roleRepository.findAll();
    return new RoleOptionsResponse(
        roles.stream()
            .filter(x -> x.getStatus() == RoleStatus.ACTIVE)
            .map(x -> new OptionStringPb(x.getName(), x.getId() + ""))
            .toList());
  }
}
