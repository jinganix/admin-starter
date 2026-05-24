package io.github.jinganix.admin.starter.sys.audit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.sys.audit.repository.AuditRepository;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.RequestMapping;

@ExtendWith(MockitoExtension.class)
@DisplayName("RequestAuditAspectUnit")
class RequestAuditAspectUnitTest {

  @Mock private AuditRepository auditRepository;

  @Mock private Emitter emitter;

  @Mock private HttpServletRequest httpServletRequest;

  @Mock private OverviewRepository overviewRepository;

  @Mock private UidGenerator uidGenerator;

  @Mock private UtilsService utilsService;

  @InjectMocks private RequestAuditAspect requestAuditAspect;

  @BeforeEach
  void setup() {
    lenient().when(httpServletRequest.getMethod()).thenReturn("POST");
    lenient().when(httpServletRequest.getRequestURI()).thenReturn("/sys/auth/token");
    lenient().when(httpServletRequest.getUserPrincipal()).thenReturn(new AuthUserToken(1L));
    lenient().when(utilsService.currentTimeMillis()).thenReturn(100L);
    lenient().when(uidGenerator.nextUid()).thenReturn(1L);
  }

  @Test
  @DisplayName("Given null join point args -> skips params serialization")
  void givenNullJoinPointArgs() {
    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getArgs()).thenReturn(null);

    requestAuditAspect.logAfter(joinPoint, mock(RequestMapping.class));

    verify(auditRepository).insert(any());
  }

  @Test
  @DisplayName("Given GET request -> skips audit insert")
  void givenGetRequest() {
    when(httpServletRequest.getMethod()).thenReturn("GET");
    JoinPoint joinPoint = mock(JoinPoint.class);

    requestAuditAspect.logAfter(joinPoint, mock(RequestMapping.class));

    verify(auditRepository, never()).insert(any());
    verify(emitter).apiCalled("GET", "/sys/auth/token");
  }
}
