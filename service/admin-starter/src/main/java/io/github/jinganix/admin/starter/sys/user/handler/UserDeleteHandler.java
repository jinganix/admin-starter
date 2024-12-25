package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.helper.exception.ApiException;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserDeleteResponse;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.admin.starter.sys.role.AdminService;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.sys.utils.BizUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeleteHandler {

  private final AdminService adminService;

  private final Emitter emitter;

  private final UserRepository userRepository;

  @Transactional
  public UserDeleteResponse handle(UserDeleteRequest request) {
    if (request.getIds().stream().anyMatch(adminService::isAdminUser)) {
      throw ApiException.of(ErrorCode.ADMIN_IS_IMMUTABLE);
    }
    List<User> users = userRepository.findAllById(request.getIds());
    if (BizUtils.notEquals(users.stream().map(User::getId).toList(), request.getIds())) {
      throw ApiException.of(ErrorCode.USER_NOT_FOUND);
    }
    userRepository.deleteAllById(request.getIds());
    emitter.userDeleted(request.getIds());
    return new UserDeleteResponse();
  }
}
