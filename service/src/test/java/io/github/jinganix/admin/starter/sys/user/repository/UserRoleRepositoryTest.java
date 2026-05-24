package io.github.jinganix.admin.starter.sys.user.repository;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.role.RoleData.role;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_4;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_5;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserRoleRepository")
class UserRoleRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserRoleRepository userRoleRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Nested
  @DisplayName("findAllByUserId")
  class FindAllByUserId {

    @Test
    @DisplayName("Given multiple users and roles -> return user roles only")
    void givenMultipleUsersAndRoles() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          user(UID_2),
          role(UID_3),
          role(UID_4),
          userRole(UID_1, UID_1, UID_3),
          userRole(UID_2, UID_1, UID_4),
          userRole(UID_3, UID_2, UID_3));

      // When
      List<UserRole> roles = userRoleRepository.findAllByUserId(UID_1);

      // Then
      assertThat(roles).extracting(UserRole::getRoleId).containsExactlyInAnyOrder(UID_3, UID_4);
    }
  }

  @Nested
  @DisplayName("findAllByUserIdIn")
  class FindAllByUserIdIn {

    @Test
    @DisplayName("Given user ids filter -> return roles for selected users")
    void givenUserIdsFilter() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          user(UID_2),
          user(UID_3),
          role(UID_4),
          role(UID_5),
          userRole(UID_1, UID_1, UID_4),
          userRole(UID_2, UID_2, UID_4),
          userRole(UID_3, UID_3, UID_5));

      // When
      List<UserRole> roles = userRoleRepository.findAllByUserIdIn(Set.of(UID_1, UID_3));

      // Then
      assertThat(roles).extracting(UserRole::getUserId).containsExactlyInAnyOrder(UID_1, UID_3);
      assertThat(roles).extracting(UserRole::getRoleId).containsExactlyInAnyOrder(UID_4, UID_5);
    }
  }

  @Nested
  @DisplayName("deleteAllByUserId")
  class DeleteAllByUserId {

    @Test
    @DisplayName("Given existing roles -> delete only target user roles")
    void givenExistingRoles() {
      // Given
      testHelper.insertEntities(
          user(UID_1),
          user(UID_2),
          role(UID_3),
          userRole(UID_1, UID_1, UID_3),
          userRole(UID_2, UID_2, UID_3));

      // When
      userRoleRepository.deleteAllByUserId(UID_1);

      // Then
      assertThat(userRoleRepository.findAllByUserId(UID_1)).isEmpty();
      assertThat(userRoleRepository.findAllByUserId(UID_2)).hasSize(1);
    }
  }

  @Nested
  @DisplayName("saveAll")
  class SaveAll {

    @Test
    @DisplayName("Given user roles -> save all records")
    void givenUserRoles() {
      // Given
      testHelper.insertEntities(user(UID_1), role(UID_2), role(UID_3));
      List<UserRole> userRoles =
          List.of(userRole(UID_4, UID_1, UID_2), userRole(UID_5, UID_1, UID_3));

      // When
      userRoleRepository.saveAll(userRoles);

      // Then
      assertThat(userRoleRepository.findAllByUserId(UID_1))
          .extracting(UserRole::getRoleId)
          .containsExactlyInAnyOrder(UID_2, UID_3);
    }
  }

  private UserRole userRole(long id, long userId, long roleId) {
    return (UserRole)
        new UserRole()
            .setId(id)
            .setUserId(userId)
            .setRoleId(roleId)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
