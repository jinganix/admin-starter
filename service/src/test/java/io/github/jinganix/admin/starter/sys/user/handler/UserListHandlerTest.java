package io.github.jinganix.admin.starter.sys.user.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.sys.user.UserListRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("UserListHandler")
class UserListHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserListHandler userListHandler;

  UserPb user1 =
      new UserPb()
          .setId(UID_1)
          .setUsername("user-10001")
          .setNickname("foo")
          .setStatus(UserStatus.ACTIVE)
          .setCreatedAt(MIN_TIMESTAMP);

  UserPb user2 =
      new UserPb()
          .setId(UID_2)
          .setUsername("user-10002")
          .setNickname("bar")
          .setStatus(UserStatus.ACTIVE)
          .setCreatedAt(MIN_TIMESTAMP);

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given no users -> return empty list")
  void givenNoUsers() {
    // Given
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    UserListRequest request =
        new UserListRequest(
            new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null, null);

    // When
    UserListResponse response = userListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(new UserListResponse(emptyList())));
  }

  @Test
  @DisplayName("Given users order by id asc -> return UID_1 first")
  void givenOrderByIdAsc() {
    // Given
    testHelper.insertEntities(
        user(UID_1).setNickname("foo"),
        user(UID_2).setNickname("bar"),
        userIdentity(UID_1),
        userIdentity(UID_2));
    Pageable pageable =
        PageRequest.of(0, 20, Sort.by(Sort.Order.asc("id"), Sort.Order.desc("nickname")));
    UserListRequest request =
        new UserListRequest(
            new PageablePb()
                .setSort(Map.of("id", SortDirection.asc, "nickname", SortDirection.desc)),
            null,
            null,
            null);

    // When
    UserListResponse response = userListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(2, new UserListResponse(List.of(user1, user2))).setPages(1));
  }

  @Test
  @DisplayName("Given users order by id desc -> return UID_2 first")
  void givenOrderByIdDesc() {
    // Given
    testHelper.insertEntities(
        user(UID_1).setNickname("foo"),
        user(UID_2).setNickname("bar"),
        userIdentity(UID_1),
        userIdentity(UID_2));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    UserListRequest request =
        new UserListRequest(
            new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null, null);

    // When
    UserListResponse response = userListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(2, new UserListResponse(List.of(user2, user1))).setPages(1));
  }
}
