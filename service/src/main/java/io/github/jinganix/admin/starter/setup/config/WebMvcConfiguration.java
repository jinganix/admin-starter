package io.github.jinganix.admin.starter.setup.config;

import io.github.jinganix.admin.starter.helper.AuthedUser;
import io.github.jinganix.admin.starter.setup.argument.mvc.UserIdArgumentResolver;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.FlexibleSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuration for {@link WebMvcConfigurer}. */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DataWebProperties.class)
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final DataWebProperties properties;

  @Bean
  SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
    return new FlexibleSortHandlerMethodArgumentResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
    return (resolver) -> {
      DataWebProperties.Pageable pageable = this.properties.getPageable();
      resolver.setPageParameterName(pageable.getPageParameter());
      resolver.setSizeParameterName(pageable.getSizeParameter());
      resolver.setOneIndexedParameters(pageable.isOneIndexedParameters());
      resolver.setPrefix(pageable.getPrefix());
      resolver.setQualifierDelimiter(pageable.getQualifierDelimiter());
      resolver.setFallbackPageable(PageRequest.of(0, pageable.getDefaultPageSize()));
      resolver.setMaxPageSize(pageable.getMaxPageSize());
    };
  }

  @Bean
  PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
    PageableHandlerMethodArgumentResolver resolver =
        new PageableHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver());
    pageableCustomizer().customize(resolver);
    return resolver;
  }

  @Bean
  @ConditionalOnMissingBean
  SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
    return resolver -> resolver.setSortParameter(this.properties.getSort().getSortParameter());
  }

  /**
   * Add argument resolvers.
   *
   * @param resolvers list of {@link HandlerMethodArgumentResolver}.
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new UserIdArgumentResolver());
    resolvers.add(pageableHandlerMethodArgumentResolver());
    resolvers.add(
        new HandlerMethodArgumentResolver() {
          @Override
          public boolean supportsParameter(MethodParameter parameter) {
            return AuthedUser.class.isAssignableFrom(parameter.getParameterType());
          }

          @Override
          public Object resolveArgument(
              MethodParameter parameter,
              ModelAndViewContainer mavContainer,
              NativeWebRequest webRequest,
              WebDataBinderFactory binderFactory) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.valueOf(authentication.getName());
            return new AuthedUser().setId(userId);
          }
        });
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    EnumValuesMap.getValuesMap()
        .forEach(
            (key, value) ->
                registry.addFormatterForFieldType(
                    key,
                    new Formatter<>() {
                      @Override
                      public Object parse(String text, Locale locale) {
                        return value.get(text);
                      }

                      @Override
                      public String print(Object object, Locale locale) {
                        return object.toString();
                      }
                    }));
  }
}
