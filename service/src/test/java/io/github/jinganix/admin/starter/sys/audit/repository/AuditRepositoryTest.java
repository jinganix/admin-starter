package io.github.jinganix.admin.starter.sys.audit.repository;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.model.AuditWithUsername;
import io.github.jinganix.admin.starter.sys.auth.repository.AdminUserIdentityRepository;
import io.github.jinganix.admin.starter.sys.user.repository.UserRepository;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("AuditRepository")
class AuditRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AuditRepository auditRepository;

  @Autowired UserRepository userRepository;

  @Autowired AdminUserIdentityRepository adminUserIdentityRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should persist and query by filters when audit entity")
  void shouldPersistAndQueryByFiltersWhenAuditEntity() {
    // Given
    testHelper.insertEntities(user(UID_1), userIdentity(UID_1));
    Audit audit = audit(UID_1, UID_1, "GET", "/adm/inserted");

    // When
    auditRepository.insert(audit);
    Page<AuditWithUsername> page =
        auditRepository.filter(PageRequest.of(0, 20), UID_1, "10001", "GET", "inserted");

    // Then
    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent())
        .singleElement()
        .satisfies(
            entity -> {
              assertThat(entity.getAudit().getId()).isEqualTo(UID_1);
              assertThat(entity.getUsername()).isEqualTo("user-10001");
            });
  }

  @Nested
  @DisplayName("when filtering audits")
  class WhenFilteringAudits {

    @Test
    @DisplayName("should return all records and allow missing identity username when null filters")
    void shouldReturnAllRecordsAndAllowMissingIdentityUsernameWhenNullFilters() {
      // Given
      testHelper.insertEntities(user(UID_1), user(UID_2), userIdentity(UID_1));
      testHelper.insertEntities(
          audit(UID_1, UID_1, "GET", "/adm/one"), audit(UID_2, UID_2, "POST", "/adm/two"));
      Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

      // When
      Page<AuditWithUsername> page = auditRepository.filter(pageable, null, null, null, null);

      // Then
      assertThat(page.getTotalElements()).isEqualTo(2);
      assertThat(page.getContent())
          .extracting(x -> x.getAudit().getId())
          .containsExactly(UID_1, UID_2);
      assertThat(page.getContent().get(0).getUsername()).isEqualTo("user-10001");
      assertThat(page.getContent().get(1).getUsername()).isNull();
    }

    @Test
    @DisplayName("should return matching records only when all filters")
    void shouldReturnMatchingRecordsOnlyWhenAllFilters() {
      // Given
      testHelper.insertEntities(user(UID_1), user(UID_2), userIdentity(UID_1), userIdentity(UID_2));
      testHelper.insertEntities(
          audit(UID_1, UID_1, "GET", "/adm/foo"), audit(UID_2, UID_2, "POST", "/adm/bar"));
      Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

      // When
      Page<AuditWithUsername> page = auditRepository.filter(pageable, UID_1, "10001", "GET", "foo");

      // Then
      assertThat(page.getTotalElements()).isEqualTo(1);
      assertThat(page.getContent()).extracting(x -> x.getAudit().getId()).containsExactly(UID_1);
      assertThat(page.getContent())
          .extracting(AuditWithUsername::getUsername)
          .containsExactly("user-10001");
    }

    @Test
    @DisplayName("should return null username when user without identity")
    void shouldReturnNullUsernameWhenUserWithoutIdentity() {
      testHelper.insertEntities(user(UID_2));
      testHelper.insertEntities(audit(UID_2, UID_2, "POST", "/adm/no-identity"));

      Page<AuditWithUsername> page =
          auditRepository.filter(PageRequest.of(0, 20), UID_2, null, null, null);

      assertThat(page.getTotalElements()).isEqualTo(1);
      assertThat(page.getContent())
          .singleElement()
          .extracting(AuditWithUsername::getUsername)
          .isNull();
    }

    @Test
    @DisplayName("should return audit with null username when deleted identity")
    void shouldReturnAuditWithNullUsernameWhenDeletedIdentity() {
      // Given
      testHelper.insertEntities(user(UID_2), userIdentity(UID_2));
      auditRepository.insert(audit(UID_2, UID_2, "POST", "/adm/deleted-user"));
      userRepository.deleteAllById(List.of(UID_2));
      adminUserIdentityRepository.deleteAllByUserIdIn(List.of(UID_2));

      // When
      Page<AuditWithUsername> pageByUserId =
          auditRepository.filter(PageRequest.of(0, 20), UID_2, null, null, null);
      Page<AuditWithUsername> pageByUsername =
          auditRepository.filter(PageRequest.of(0, 20), null, "user-10002", null, null);

      // Then
      assertThat(pageByUserId.getTotalElements()).isEqualTo(1);
      assertThat(pageByUserId.getContent())
          .singleElement()
          .satisfies(
              entity -> {
                assertThat(entity.getAudit().getUserId()).isEqualTo(UID_2);
                assertThat(entity.getAudit().getPath()).isEqualTo("/adm/deleted-user");
                assertThat(entity.getUsername()).isNull();
              });
      assertThat(pageByUsername.getTotalElements()).isZero();
    }
  }

  private Audit audit(long id, long userId, String method, String path) {
    return (Audit)
        new Audit()
            .setId(id)
            .setUserId(userId)
            .setMethod(method)
            .setPath(path)
            .setParams("{}")
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
