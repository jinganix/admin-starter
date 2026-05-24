package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.helper.AuthedUser;
import io.github.jinganix.admin.starter.setup.argument.mvc.UserIdArgumentResolver;
import io.github.jinganix.admin.starter.sys.permission.model.PermissionType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@DisplayName("WebMvcConfiguration")
class WebMvcConfigurationTest {

  private final WebMvcConfiguration configuration = new WebMvcConfiguration();

  @AfterEach
  void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("should uses fallback pageable when missing pageable")
  void shouldUsesFallbackPageableWhenMissingPageable() throws Exception {
    PageableHandlerMethodArgumentResolver resolver =
        configuration.pageableHandlerMethodArgumentResolver();
    MethodParameter parameter =
        new MethodParameter(
            SampleController.class.getDeclaredMethod(
                "list", org.springframework.data.domain.Pageable.class),
            0);

    Object resolved =
        resolver.resolveArgument(
            parameter, null, new ServletWebRequest(new MockHttpServletRequest()), null);

    assertThat(resolved).isEqualTo(PageRequest.of(0, 20));
  }

  @Test
  @DisplayName("should sets sort parameter when resolver")
  void shouldSetsSortParameterWhenResolver() {
    org.springframework.data.web.SortHandlerMethodArgumentResolver resolver =
        new org.springframework.data.web.SortHandlerMethodArgumentResolver();

    configuration.sortCustomizer().customize(resolver);

    assertThat(configuration.sortCustomizer()).isNotNull();
  }

  @Test
  @DisplayName("should registers user id and authed user resolvers when resolvers list")
  void shouldRegistersUserIdAndAuthedUserResolversWhenResolversList() throws Exception {
    List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
    configuration.addArgumentResolvers(resolvers);

    assertThat(resolvers).hasSize(3);
    assertThat(resolvers.get(0)).isInstanceOf(UserIdArgumentResolver.class);

    HandlerMethodArgumentResolver authedUserResolver = resolvers.get(2);
    MethodParameter parameter =
        new MethodParameter(
            SampleController.class.getDeclaredMethod("current", AuthedUser.class), 0);
    assertThat(authedUserResolver.supportsParameter(parameter)).isTrue();

    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("42", "n/a"));
    Object resolved =
        authedUserResolver.resolveArgument(
            parameter, null, new ServletWebRequest(new MockHttpServletRequest()), null);

    assertThat(resolved).isInstanceOf(AuthedUser.class);
    assertThat(((AuthedUser) resolved).getId()).isEqualTo(42L);
  }

  @Test
  @DisplayName("should registers enum formatters when registry")
  void shouldRegistersEnumFormattersWhenRegistry() {
    FormattingConversionService conversionService = new FormattingConversionService();

    configuration.addFormatters(conversionService);

    assertThat(conversionService.convert("1", PermissionType.class)).isEqualTo(PermissionType.API);
    assertThat(conversionService.convert(PermissionType.API, String.class))
        .isEqualTo(String.valueOf(PermissionType.API));
  }

  static class SampleController {

    void list(org.springframework.data.domain.Pageable pageable) {}

    void current(AuthedUser user) {}
  }
}
