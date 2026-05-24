package io.github.jinganix.admin.starter.adm.overview;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.adm.role.PermissionUtils;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListRequest;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListResponse;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.sys.permission.Authority;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("OverviewController")
class OverviewControllerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired OverviewMapper overviewMapper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return BAD_TOKEN when malformed bearer token")
  void shouldReturnBadTokenWhenMalformedBearerToken() throws Exception {
    // Given
    String malformedToken = "malformed-token";

    // When / Then
    testHelper
        .request(malformedToken, new OverviewListRequest())
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_TOKEN));
    verify(credentialsAuthenticator, never()).authenticate(any());
  }

  @Test
  @DisplayName("should return BAD_TOKEN when expired bearer token")
  void shouldReturnBadTokenWhenExpiredBearerToken() throws Exception {
    // Given
    String token = tokenService.generate(UID_1);
    when(utilsService.currentTimeMillis())
        .thenReturn(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8));

    // When / Then
    testHelper
        .request(token, new OverviewListRequest())
        .andExpect(status().isUnauthorized())
        .andExpect(testHelper.isError(ErrorCode.BAD_TOKEN));
    verify(credentialsAuthenticator, never()).authenticate(any());
  }

  @Test
  @DisplayName("should return USER_IS_INACTIVE when inactive user token")
  void shouldReturnUserIsInactiveWhenInactiveUserToken() throws Exception {
    // Given
    String token = tokenService.generate(UID_1);
    doReturn(true).when(userInactiveChecker).isInactive(UID_1);

    // When / Then
    testHelper
        .request(token, new OverviewListRequest())
        .andExpect(status().isForbidden())
        .andExpect(testHelper.isError(ErrorCode.USER_IS_INACTIVE));
    verify(credentialsAuthenticator, never()).authenticate(any());
  }

  @Test
  @DisplayName("should return ACCESS_DENIED when missing ADM_OVERVIEW_LIST permission")
  void shouldReturnAccessDeniedWhenMissingAdmOverviewListPermission() throws Exception {
    // Given / When / Then
    testHelper
        .request(UID_1, new OverviewListRequest())
        .andExpect(testHelper.isError(ErrorCode.ACCESS_DENIED));
  }

  @Test
  @DisplayName("should return overview list when ADM_OVERVIEW_LIST permission")
  void shouldReturnOverviewListWhenAdmOverviewListPermission() throws Exception {
    // Given
    when(roleAuthorityService.getApiAuthorities(UID_1))
        .thenReturn(PermissionUtils.permissions(Authority.ADM_OVERVIEW_LIST));
    LocalDate includedMonth = LocalDate.now().withDayOfMonth(2).minusMonths(1).withDayOfMonth(1);
    Overview overview =
        (Overview)
            new Overview()
                .setId(UID_1)
                .setMonth(includedMonth)
                .setCreatedAt(MIN_TIMESTAMP)
                .setUpdatedAt(MIN_TIMESTAMP);
    testHelper.insertEntities(overview);

    // When / Then
    testHelper
        .request(UID_1, new OverviewListRequest())
        .andExpect(status().isOk())
        .andExpect(
            testHelper.isResponse(
                new OverviewListResponse(java.util.List.of(overviewMapper.overviewPb(overview)))));
  }
}
