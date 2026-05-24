package io.github.jinganix.admin.starter.sys.user.repository;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserStatus;
import io.github.jinganix.admin.starter.sys.user.model.UserWithUsername;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("UserRepository")
class UserRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired UserRepository userRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return mapped username when user and identity")
  void shouldReturnMappedUsernameWhenUserAndIdentity() {
    // Given
    testHelper.insertEntities(user(UID_1), userIdentity(UID_1));

    // When
    UserWithUsername entity = userRepository.findByIdWithUsername(UID_1);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getUser().getId()).isEqualTo(UID_1);
    assertThat(entity.getUsername()).isEqualTo("user-10001");
  }

  @Test
  @DisplayName("should return all users when null filters")
  void shouldReturnAllUsersWhenNullFilters() {
    // Given
    testHelper.insertEntities(
        user(UID_1).setStatus(UserStatus.ACTIVE),
        user(UID_2).setStatus(UserStatus.INACTIVE),
        userIdentity(UID_1),
        userIdentity(UID_2));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

    // When
    Page<UserWithUsername> page = userRepository.filter(pageable, null, null, null);

    // Then
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent())
        .extracting(x -> x.getUser().getId())
        .containsExactly(UID_1, UID_2);
  }

  @Test
  @DisplayName("should return matching user only when all filters")
  void shouldReturnMatchingUserOnlyWhenAllFilters() {
    // Given
    testHelper.insertEntities(
        user(UID_1).setStatus(UserStatus.ACTIVE),
        user(UID_2).setStatus(UserStatus.INACTIVE),
        userIdentity(UID_1),
        userIdentity(UID_2));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

    // When
    Page<UserWithUsername> page =
        userRepository.filter(pageable, UID_2, "10002", UserStatus.INACTIVE);

    // Then
    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent())
        .singleElement()
        .satisfies(
            entity -> {
              assertThat(entity.getUser().getId()).isEqualTo(UID_2);
              assertThat(entity.getUsername()).isEqualTo("user-10002");
            });
  }

  @Test
  @DisplayName("should return entity when user exists")
  void shouldReturnEntityWhenUserExists() {
    // Given
    testHelper.insertEntities(user(UID_1).setNickname("u1"));

    // When
    User found = userRepository.findById(UID_1);

    // Then
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(UID_1);
    assertThat(found.getNickname()).isEqualTo("u1");
  }

  @Test
  @DisplayName("should return matching users when ids list")
  void shouldReturnMatchingUsersWhenIdsList() {
    // Given
    testHelper.insertEntities(user(UID_1), user(UID_2), user(UID_3));

    // When
    List<User> users = userRepository.findAllById(List.of(UID_1, UID_3));

    // Then
    assertThat(users).extracting(User::getId).containsExactlyInAnyOrder(UID_1, UID_3);
  }

  @Test
  @DisplayName("should remove only matching users when ids to delete")
  void shouldRemoveOnlyMatchingUsersWhenIdsToDelete() {
    // Given
    testHelper.insertEntities(user(UID_1), user(UID_2), user(UID_3));

    // When
    userRepository.deleteAllById(List.of(UID_1, UID_3));
    List<User> remaining = userRepository.findAllById(List.of(UID_1, UID_2, UID_3));

    // Then
    assertThat(remaining).extracting(User::getId).containsExactly(UID_2);
  }

  @Test
  @DisplayName("should persist values when user entity")
  void shouldPersistValuesWhenUserEntity() {
    // Given
    User entity = userEntity(UID_1, "before", UserStatus.ACTIVE);

    // When
    userRepository.insert(entity);
    User inserted = userRepository.findById(UID_1);

    // Then
    assertThat(inserted).isNotNull();
    assertThat(inserted.getNickname()).isEqualTo("before");
    assertThat(inserted.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("should update mutable fields when existing user")
  void shouldUpdateMutableFieldsWhenExistingUser() {
    // Given
    userRepository.insert(userEntity(UID_1, "before", UserStatus.ACTIVE));
    User changed = userEntity(UID_1, "after", UserStatus.INACTIVE);

    // When
    userRepository.update(changed);
    User updated = userRepository.findById(UID_1);

    // Then
    assertThat(updated).isNotNull();
    assertThat(updated.getNickname()).isEqualTo("after");
    assertThat(updated.getStatus()).isEqualTo(UserStatus.INACTIVE);
  }

  private User userEntity(long id, String nickname, UserStatus status) {
    return (User)
        new User()
            .setId(id)
            .setNickname(nickname)
            .setStatus(status)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
