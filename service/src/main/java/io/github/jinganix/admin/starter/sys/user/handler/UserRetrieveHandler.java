package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserRetrieveResponse;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserRetrieveHandler {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  private final UserRoleRepository userRoleRepository;

  @Transactional
  public UserRetrieveResponse handle(UserRetrieveRequest request) {
    UserWithUsername user =
        userRepository
            .findByIdWithUsername(request.getId())
            .orElseThrow(() -> ApiException.of(ErrorCode.USER_NOT_FOUND));
    List<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getUser().getId());
    return new UserRetrieveResponse(
        userMapper.detailsPb(user, userRoles.stream().map(UserRole::getRoleId).toList()));
  }
}
