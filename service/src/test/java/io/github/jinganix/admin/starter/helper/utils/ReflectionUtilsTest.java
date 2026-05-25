package io.github.jinganix.admin.starter.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

@DisplayName("ReflectionUtils")
class ReflectionUtilsTest {

  @Test
  @DisplayName("should should create instance when default constructor")
  void shouldShouldCreateInstanceWhenDefaultConstructor() {
    assertThat(new ReflectionUtils()).isNotNull();
  }

  @Test
  @DisplayName("should should return matching classes when package name")
  void shouldShouldReturnMatchingClassesWhenPackageName() {
    var classes = ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName());

    assertThat(classes).contains(ReflectionUtils.class, UtilsService.class);
  }

  @Test
  @DisplayName("should should throw illegal state when classpath read failure")
  void shouldShouldThrowIllegalStateWhenClasspathReadFailure() {
    try (MockedStatic<ClassPath> mocked = mockStatic(ClassPath.class)) {
      mocked.when(() -> ClassPath.from(any(ClassLoader.class))).thenThrow(new IOException("test"));

      assertThatThrownBy(
              () -> ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName()))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Failed to read classpath");
    }
  }

  @Test
  @DisplayName("should should load class when boot inf prefixed name")
  void shouldShouldLoadClassWhenBootInfPrefixedName() {
    ClassLoader classLoader = ReflectionUtils.class.getClassLoader();
    String bootInfName = "BOOT-INF.classes." + ReflectionUtils.class.getName();

    assertThat(ReflectionUtils.loadClass(bootInfName, classLoader))
        .isEqualTo(ReflectionUtils.class);
    assertThat(ReflectionUtils.normalizeClassName(bootInfName))
        .isEqualTo(ReflectionUtils.class.getName());
  }

  @Test
  @DisplayName("should should throw illegal state when missing class name")
  void shouldShouldThrowIllegalStateWhenMissingClassName() {
    assertThatThrownBy(
            () ->
                ReflectionUtils.loadClass(
                    "missing.ClassName", ReflectionUtils.class.getClassLoader()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Failed to load class");
  }
}
