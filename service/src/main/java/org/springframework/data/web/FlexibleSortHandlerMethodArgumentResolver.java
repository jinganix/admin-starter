package org.springframework.data.web;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriUtils;

/** Supports semicolon-separated sort parameters. */
public class FlexibleSortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolver {

  @Override
  public Sort resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    String[] values = webRequest.getParameterValues(getSortParameter(parameter));
    if (values == null) {
      return getDefaultFromAnnotationOrFallback(parameter);
    }
    String[] directionParameter =
        Arrays.stream(values).map(x -> x.split(";")).flatMap(Stream::of).toArray(String[]::new);

    if (directionParameter.length == 1 && !StringUtils.hasText(directionParameter[0])) {
      return getDefaultFromAnnotationOrFallback(parameter);
    }

    var decoded =
        Arrays.stream(directionParameter)
            .map(it -> UriUtils.decode(it, StandardCharsets.UTF_8))
            .toList();

    return parseParameterIntoSort(decoded, getPropertyDelimiter());
  }
}
