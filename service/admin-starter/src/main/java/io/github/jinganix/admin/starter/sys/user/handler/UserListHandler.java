package io.github.jinganix.admin.starter.sys.user.handler;

import io.github.jinganix.admin.starter.proto.sys.user.UserListRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserListHandler {

  private final UserMapper userMapper;

  private final UserRepository userRepository;

  public UserListResponse handle(Pageable pageable, UserListRequest request) {
    Page<UserWithUsername> users =
        userRepository.filter(
            pageable,
            request.getUserId(),
            request.getUsername(),
            userMapper.status(request.getStatus()));
    return userMapper.page(users);
  }
}
