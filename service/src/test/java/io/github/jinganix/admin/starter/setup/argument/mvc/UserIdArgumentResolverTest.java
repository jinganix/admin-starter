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
  @DisplayName("when checking parameter support")
  class WhenCheckingParameterSupport {

    @Test
    @DisplayName("should return true when @UserId parameter")
    void shouldReturnTrueWhenUserIdParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(SampleController.class.getDeclaredMethod("method", Long.class), 0);
      assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("should return true when meta @UserId parameter")
    void shouldReturnTrueWhenMetaUserIdParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("wrappedMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("should return false when plain parameter")
    void shouldReturnFalseWhenPlainParameter() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("plainMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    @DisplayName("should return false when getattr with other annotation")
    void shouldReturnFalseWhenGetattrWithOtherAnnotation() throws NoSuchMethodException {
      MethodParameter parameter =
          new MethodParameter(
              SampleController.class.getDeclaredMethod("requestParamMethod", Long.class), 0);

      assertThat(resolver.supportsParameter(parameter)).isFalse();
    }
  }

  @Nested
  @DisplayName("when resolving user id argument")
  class WhenResolvingUserIdArgument {

    @Test
    @DisplayName("should return user id when AuthUserToken principal")
    void shouldReturnUserIdWhenAuthUserTokenPrincipal() throws Exception {
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
    @DisplayName("should return null when missing principal")
    void shouldReturnNullWhenMissingPrincipal() throws Exception {
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
    @DisplayName("should return user id when meta @UserId parameter")
    void shouldReturnUserIdWhenMetaUserIdParameter() throws Exception {
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
    @DisplayName("should throws illegal state when non-servlet request")
    void shouldThrowsIllegalStateWhenNonServletRequest() throws Exception {
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
