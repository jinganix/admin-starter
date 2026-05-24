package io.github.jinganix.admin.starter.setup.argument.mvc;

import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.admin.starter.helper.auth.token.AuthUserToken;
import io.github.jinganix.admin.starter.setup.argument.annotations.UserId;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

@DisplayName("UserIdArgumentResolver")
class UserIdArgumentResolverTest {

  private final UserIdArgumentResolver resolver = new UserIdArgumentResolver();

  static class SampleController {

    void method(@UserId Long userId) {}

    void wrappedMethod(@WrappedUserId Long userId) {}

    void plainMethod(Long userId) {}

    void requestParamMethod(@RequestParam Long userId) {}
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @UserId
  @interface WrappedUserId {}

  @Nested
  @DisplayName("supportsParameter")
  class SupportsParameter {

    @Test
    @DisplayName("Given @UserId parameter -> returns true")
    void givenUserIdParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getDeclaredMethod("method", Long.class), 0);
      assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("Given meta @UserId parameter -> returns true")
    void givenMetaUserIdParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("wrappedMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("Given plain parameter -> returns false")
    void givenPlainParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("plainMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    @DisplayName("Given getattr with other annotation -> returns false")
    void givenOtherAnnotation() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("requestParamMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isFalse();
    }
  }

  @Nested
  @DisplayName("resolveArgument")
  class ResolveArgument {

    @Test
    @DisplayName("Given AuthUserToken principal -> returns user id")
    void givenAuthUserTokenPrincipal() throws Exception {
      // Given
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getDeclaredMethod("method", Long.class), 0);
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.setUserPrincipal(new AuthUserToken(UID_1));
      ServletWebRequest webRequest = new ServletWebRequest(request);

      // When
      Object result = resolver.resolveArgument(parameter, null, webRequest, null);

      // Then
      assertThat(result).isEqualTo(UID_1);
    }

    @Test
    @DisplayName("Given missing principal -> returns null")
    void givenMissingPrincipal() throws Exception {
      // Given
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getDeclaredMethod("method", Long.class), 0);
      ServletWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

      // When
      Object result = resolver.resolveArgument(parameter, null, webRequest, null);

      // Then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Given meta @UserId parameter -> returns user id")
    void givenMetaUserIdParameter() throws Exception {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("wrappedMethod", Long.class), 0);
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.setUserPrincipal(new AuthUserToken(UID_1));
      ServletWebRequest webRequest = new ServletWebRequest(request);

      Object result = resolver.resolveArgument(parameter, null, webRequest, null);

      assertThat(result).isEqualTo(UID_1);
    }

    @Test
    @DisplayName("Given non-servlet request -> throws illegal state")
    void givenNonServletRequest() throws Exception {
      // Given
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getDeclaredMethod("method", Long.class), 0);

      // When / Then
      NativeWebRequest webRequest = mock(NativeWebRequest.class);
      when(webRequest.getNativeRequest(jakarta.servlet.http.HttpServletRequest.class))
          .thenReturn(null);

      assertThatThrownBy(() -> resolver.resolveArgument(parameter, null, webRequest, null))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("HttpServletRequest");
    }
  }
}
