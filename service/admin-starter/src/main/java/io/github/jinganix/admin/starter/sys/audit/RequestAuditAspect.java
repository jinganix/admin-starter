package io.github.jinganix.admin.starter.sys.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jinganix.admin.starter.adm.overview.repository.OverviewRepository;
import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.audit.repository.AuditRepository;
import io.github.jinganix.admin.starter.sys.emitter.Emitter;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestAuditAspect {

  private final AuditRepository auditRepository;

  private final Emitter emitter;

  private final HttpServletRequest httpServletRequest;

  private final OverviewRepository overviewRepository;

  private final UidGenerator uidGenerator;

  private final UtilsService utilsService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Transactional
  @After("@annotation(requestMapping) && execution(* *(..))")
  public void logAfter(JoinPoint joinPoint, RequestMapping requestMapping) {
    String httpMethod = httpServletRequest.getMethod();
    emitter.apiCalled(httpMethod.toUpperCase(), httpServletRequest.getRequestURI());
    if (HttpMethod.GET.name().equalsIgnoreCase(httpMethod)) {
      return;
    }
    try {
      AuthUserToken token = (AuthUserToken) httpServletRequest.getUserPrincipal();
      if (token == null) {
        return;
      }
      long millis = utilsService.currentTimeMillis();
      Audit audit =
          (Audit)
              new Audit()
                  .setId(uidGenerator.nextUid())
                  .setUserId(token.getUserId())
                  .setMethod(httpMethod)
                  .setPath(httpServletRequest.getRequestURI())
                  .setCreatedAt(millis)
                  .setUpdatedAt(millis);
      if (joinPoint.getArgs() != null) {
        for (Object arg : joinPoint.getArgs()) {
          if (arg instanceof WebpbMessage message) {
            audit.setParams(objectMapper.writeValueAsString(message));
          }
        }
      }
      auditRepository.save(audit);
    } catch (Exception e) {
      log.error("", e);
    }
  }
}
