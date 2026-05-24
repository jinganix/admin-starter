package io.github.jinganix.admin.starter.sys.audit;

import static io.github.jinganix.admin.starter.sys.auth.AuthData.user;
import static io.github.jinganix.admin.starter.sys.user.UserData.userIdentity;
import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.lib.pageable.PageablePb;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListRequest;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditListResponse;
import io.github.jinganix.admin.starter.proto.sys.audit.AuditPb;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("AuditController")
class AuditControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing SYS_AUDIT_LIST permission")
  void shouldReturnAccessDeniedWhenMissingSysAuditListPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new AuditListRequest(new PageablePb(), null, null, null, null))
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return audit list when SYS_AUDIT_LIST permission")
  void shouldReturnAuditListWhenSysAuditListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.SYS_AUDIT_LIST));
    testHelper.insertEntities(
        user(UID_1),
        userIdentity(UID_1),
        new Audit()
            .setId(UID_3)
            .setUserId(UID_1)
            .setMethod("POST")
            .setPath("/adm/sys/user/create")
            .setParams("{}")
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP));
    AuditPb auditPb =
        new AuditPb()
            .setId(UID_3)
            .setUserId(UID_1)
            .setUsername("user-10001")
            .setMethod("POST")
            .setPath("/adm/sys/user/create")
            .setCreatedAt(MIN_TIMESTAMP);

    // When / Then
    testHelper
        .request(UID_1, new AuditListRequest(new PageablePb(), null, null, null, null))
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                testHelper
                    .paging(1, new AuditListResponse(java.util.List.of(auditPb)))
                    .setPages(1)));
  }
}
