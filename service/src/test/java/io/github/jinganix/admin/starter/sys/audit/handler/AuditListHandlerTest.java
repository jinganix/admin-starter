package io.github.jinganix.admin.starter.sys.audit.handler;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.lib.pageable.SortDirection;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListRequest;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListResponse;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditPb;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
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

@DisplayName("AuditListHandler")
class AuditListHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired AuditListHandler auditListHandler;

  AuditPb audit1 =
      new AuditPb()
          .setId(UID_1)
          .setUserId(UID_1)
          .setUsername("user-10001")
          .setMethod("GET")
          .setPath("/adm/foo")
          .setCreatedAt(MIN_TIMESTAMP);

  AuditPb audit2 =
      new AuditPb()
          .setId(UID_2)
          .setUserId(UID_2)
          .setUsername("user-10002")
          .setMethod("POST")
          .setPath("/adm/bar")
          .setCreatedAt(MIN_TIMESTAMP);

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return empty list when no audits")
  void shouldReturnEmptyListWhenNoAudits() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    AuditListRequest request = new AuditListRequest(new PageablePb(), null, null, null, null);

    // When
    AuditListResponse response = auditListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(new AuditListResponse(emptyList())));
  }

  @Test
  @DisplayName("should return all records when audits")
  void shouldReturnAllRecordsWhenAudits() {
    // Given
    testHelper.insertEntities(
        user(UID_1),
        user(UID_2),
        userIdentity(UID_1),
        userIdentity(UID_2),
        audit(UID_1, UID_1, "GET", "/adm/foo"),
        audit(UID_2, UID_2, "POST", "/adm/bar"));
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
    AuditListRequest request =
        new AuditListRequest(
            new PageablePb().setSort(Map.of("id", SortDirection.asc)), null, null, null, null);

    // When
    AuditListResponse response = auditListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(
            testHelper.paging(2, new AuditListResponse(List.of(audit1, audit2))).setPages(1));
  }

  @Test
  @DisplayName("should return matching records when userId filter")
  void shouldReturnMatchingRecordsWhenUserIdFilter() {
    // Given
    testHelper.insertEntities(
        user(UID_1),
        user(UID_2),
        userIdentity(UID_1),
        userIdentity(UID_2),
        audit(UID_1, UID_1, "GET", "/adm/foo"),
        audit(UID_2, UID_2, "POST", "/adm/bar"));
    Pageable pageable = PageRequest.of(0, 20);
    AuditListRequest request = new AuditListRequest(new PageablePb(), UID_1, null, null, null);

    // When
    AuditListResponse response = auditListHandler.handle(pageable, request);

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(testHelper.paging(1, new AuditListResponse(List.of(audit1))).setPages(1));
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
