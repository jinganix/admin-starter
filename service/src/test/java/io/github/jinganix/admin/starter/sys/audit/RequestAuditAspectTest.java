package io.github.jinganix.admin.starter.sys.audit;

import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListRequest;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthLoginRequest;
import io.github.jinganix.admin.starter.proto.sys.auth.AuthTokenRequest;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.repository.AuditRepository;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@DisplayName("RequestAuditAspect")
class RequestAuditAspectTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;
  @Autowired AuditRepository auditRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("Given authenticated GET request -> skip writing audit")
  void givenGetRequest() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.ADM_OVERVIEW_LIST));

    // When
    testHelper.request(UID_1, new OverviewListRequest()).andExpect(status().isOk());

    // Then
    assertThat(readAudits()).isEmpty();
  }

  @Test
  @DisplayName("Given unauthenticated POST request -> skip writing audit")
  void givenPostRequestWithoutPrincipal() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1)).thenReturn(Set.of());
    when(uidGenerator.nextUid()).thenReturn(UID_1);
    when(utilsService.uuid(anyBoolean())).thenReturn("test_uuid");
    doReturn(new AuthUserToken(UID_1)).when(credentialsAuthenticator).authenticate(any());

    // When
    testHelper.request(new AuthLoginRequest("aaaaaa", "aaaaaa")).andExpect(status().isOk());

    // Then
    assertThat(readAudits()).isEmpty();
  }

  @Test
  @DisplayName("Given authenticated POST request -> create audit record")
  void givenPostRequestWithPrincipal() throws Exception {
    // Given
    AuthTokenRequest request = new AuthTokenRequest("missing-refresh-token");

    // When
    testHelper
        .request(UID_1, request)
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_REFRESH_TOKEN));

    // Then
    java.util.List<Audit> audits = readAudits();
    assertThat(audits).hasSize(1);
    Audit audit = audits.get(0);
    assertThat(audit.getUserId()).isEqualTo(UID_1);
    assertThat(audit.getMethod()).isEqualTo("POST");
    assertThat(audit.getPath()).isEqualTo(WebpbUtils.formatUrl(request));
  }

  @Test
  @DisplayName("Given audit creation throws exception -> swallow and skip audit")
  void givenAuditCreationThrowsException() throws Exception {
    // Given
    AuthTokenRequest request = new AuthTokenRequest("missing-refresh-token");
    when(uidGenerator.nextUid()).thenThrow(new RuntimeException("test"));

    // When
    testHelper
        .request(UID_1, request)
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_REFRESH_TOKEN));

    // Then
    assertThat(readAudits()).isEmpty();
  }

  private java.util.List<Audit> readAudits() {
    return auditRepository
        .filter(PageRequest.of(0, 20), null, null, null, null)
        .getContent()
        .stream()
        .map(item -> item.getAudit())
        .toList();
  }
}
