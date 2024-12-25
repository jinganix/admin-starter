package io.github.jinganix.admin.starter.tests;

import static io.github.jinganix.admin.starter.tests.TestConst.MILLIS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import io.github.jinganix.admin.starter.helper.auth.UserInactiveChecker;
import io.github.jinganix.admin.starter.helper.auth.token.TokenService;
import io.github.jinganix.admin.starter.helper.uid.UidGenerator;
import io.github.jinganix.admin.starter.helper.utils.UtilsService;
import io.github.jinganix.admin.starter.sys.auth.authenticator.CredentialsAuthenticator;
import io.github.jinganix.admin.starter.sys.role.RoleAuthorityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** Tests for spring mvc. */
@ContextConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith({MysqlExtension.class})
@ExtendWith({RedisExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class SpringBootIntegrationTests {

  @MockitoSpyBean protected CredentialsAuthenticator credentialsAuthenticator;

  @MockitoSpyBean protected RoleAuthorityService roleAuthorityService;

  @MockitoSpyBean protected TokenService tokenService;

  @MockitoSpyBean protected UidGenerator uidGenerator;

  @MockitoSpyBean protected UserInactiveChecker userInactiveChecker;

  @MockitoSpyBean protected UtilsService utilsService;

  @BeforeEach
  protected void commonSetup() {
    doReturn(false).when(userInactiveChecker).isInactive(anyLong());
    when(utilsService.currentTimeMillis()).thenReturn(MILLIS);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .apply(springSecurity())
          .build();
    }
  }
}
