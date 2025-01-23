package io.github.jinganix.admin.starter.sys.user.controller;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.user.UserListRequest;
import io.github.jinganix.admin.starter.proto.sys.user.UserListResponse;
import io.github.jinganix.admin.starter.proto.sys.user.UserPb;
import io.github.jinganix.admin.starter.proto.sys.user.UserStatus;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserController$list")
class UserListControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("when check fails")
  class WhenCheckFails {

    @Nested
    @DisplayName("when missing `GET_USERS` permission")
    class WhenMissingGetUsersUser {

      @Test
      @DisplayName("then response forbidden")
      void thenResponseForbidden() throws Exception {
        testHelper
            .request(UID_1, new UserListRequest())
            .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
      }
    }
  }

  @Nested
  @DisplayName("when request is performed")
  class WhenRequestIsPerformed {

    @BeforeEach
    void setup() {
      when(roleAuthorityService.getApiAuthorities(UID_1))
          .thenReturn(PermissionUtils.permissions(Authority.SYS_USER_LIST));
    }

    @Nested
    @DisplayName("when no users")
    class WhenNoUsers {

      @Test
      @DisplayName("then response empty list")
      void thenResponseEmptyList() throws Exception {
        testHelper
            .request(
                UID_1,
                new UserListRequest(
                    new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null))
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    assertThat(testHelper.deserialize(result, UserListResponse.class))
                        .usingRecursiveComparison()
                        .isEqualTo(testHelper.paging(new UserListResponse(emptyList()))));
      }
    }

    UserPb user1 = new UserPb().setId(UID_1).setNickname("foo").setStatus(UserStatus.ACTIVE);
    UserPb user2 = new UserPb().setId(UID_2).setNickname("bar").setStatus(UserStatus.ACTIVE);

    @Nested
    @DisplayName("when order by id asc")
    class WhenOrderByIdAsc {

      @Test
      @DisplayName("then response UID_1 first")
      void thenResponseUID1First() throws Exception {
        testHelper.insertEntities(user(UID_1).setNickname("foo"), user(UID_2).setNickname("bar"));

        testHelper
            .request(
                UID_1,
                new UserListRequest(
                    new PageablePb()
                        .setSort(Map.of("id", SortDirection.asc, "nickname", SortDirection.desc)),
                    null,
                    null))
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    testHelper.isResponse(
                        testHelper.paging(2, new UserListResponse(List.of(user1, user2)))));
      }
    }

    @Nested
    @DisplayName("when order by id desc")
    class WhenOrderByIdDesc {

      @Test
      @DisplayName("then response UID_2 first")
      void thenResponseUID2First() throws Exception {
        testHelper.insertEntities(user(UID_1).setNickname("foo"), user(UID_2).setNickname("bar"));

        testHelper
            .request(
                UID_1,
                new UserListRequest(
                    new PageablePb().setSort(Map.of("id", SortDirection.desc)), null, null))
            .andExpect(status().isOk())
            .andExpect(
                result ->
                    testHelper.isResponse(
                        testHelper.paging(2, new UserListResponse(List.of(user2, user1)))));
      }
    }
  }
}
