package io.github.jinganix.admin.starter.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

@DisplayName("ReflectionUtils")
class ReflectionUtilsTest {

  @Test
  @DisplayName("Given default constructor -> should create instance")
  void givenDefaultConstructorShouldCreateInstance() {
    assertThat(new ReflectionUtils()).isNotNull();
  }

  @Nested
  @DisplayName("findAllClasses")
  class FindAllClasses {

    @Test
    @DisplayName("Given package name -> should return matching classes")
    void givenPackageNameShouldReturnMatchingClasses() {
      var classes = ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName());

      assertThat(classes).contains(ReflectionUtils.class, UtilsService.class);
    }

    @Test
    @DisplayName("Given classpath read failure -> should throw illegal state")
    void givenClasspathReadFailureShouldThrowIllegalState() {
      try (MockedStatic<ClassPath> mocked = mockStatic(ClassPath.class)) {
        mocked
            .when(() -> ClassPath.from(any(ClassLoader.class)))
            .thenThrow(new IOException("test"));

        assertThatThrownBy(
                () -> ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Failed to read classpath");
      }
    }
  }

  @Nested
  @DisplayName("loadClass")
  class LoadClass {

    @Test
    @DisplayName("Given missing class name -> should throw illegal state")
    void givenMissingClassNameShouldThrowIllegalState() {
      assertThatThrownBy(
              () ->
                  ReflectionUtils.loadClass(
                      "missing.ClassName", ReflectionUtils.class.getClassLoader()))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Failed to load class");
    }
  }
}
