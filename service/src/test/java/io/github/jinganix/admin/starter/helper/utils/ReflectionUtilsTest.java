package io.github.jinganix.admin.starter.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

@DisplayName("ReflectionUtils")
class ReflectionUtilsTest {

  @Test
  @DisplayName("should create instance when default constructor")
  void shouldCreateInstanceWhenDefaultConstructor() {
    // Given / When
    assertThat(new ReflectionUtils()).isNotNull();
  }

  @Test
  @DisplayName("should return matching classes when package name")
  void shouldReturnMatchingClassesWhenPackageName() {
    // Given / When
    var classes = ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName());

    // Then
    assertThat(classes).contains(ReflectionUtils.class, UtilsService.class);
  }

  @Test
  @DisplayName("should throw illegal state when classpath read failure")
  void shouldThrowIllegalStateWhenClasspathReadFailure() {
    // Given
    try (MockedStatic<ClassPath> mocked = mockStatic(ClassPath.class)) {
      mocked.when(() -> ClassPath.from(any(ClassLoader.class))).thenThrow(new IOException("test"));

      // When / Then
      assertThatThrownBy(
              () -> ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName()))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Failed to read classpath");
    }
  }

  @Test
  @DisplayName("should load class when BOOT-INF dot prefixed name")
  void shouldLoadClassWhenBootInfDotPrefixedName() {
    // Given
    ClassLoader classLoader = ReflectionUtils.class.getClassLoader();
    String bootInfName = "BOOT-INF.classes." + ReflectionUtils.class.getName();

    // When / Then
    assertThat(ReflectionUtils.loadClass(bootInfName, classLoader))
        .isEqualTo(ReflectionUtils.class);
    assertThat(ReflectionUtils.normalizeClassName(bootInfName))
        .isEqualTo(ReflectionUtils.class.getName());
  }

  @Test
  @DisplayName("should load class when BOOT-INF path prefixed name")
  void shouldLoadClassWhenBootInfPathPrefixedName() {
    // Given
    ClassLoader classLoader = ReflectionUtils.class.getClassLoader();
    String bootInfPathName =
        "BOOT-INF/classes/" + ReflectionUtils.class.getName().replace('.', '/');

    // When / Then
    assertThat(ReflectionUtils.loadClass(bootInfPathName, classLoader))
        .isEqualTo(ReflectionUtils.class);
    assertThat(ReflectionUtils.normalizeClassName(bootInfPathName))
        .isEqualTo(ReflectionUtils.class.getName());
  }

  @Test
  @DisplayName("should return original class name when no BOOT-INF prefix")
  void shouldReturnOriginalClassNameWhenNoBootInfPrefix() {
    // Given
    String className = ReflectionUtils.class.getName();

    // When
    String normalized = ReflectionUtils.normalizeClassName(className);

    // Then
    assertThat(normalized).isEqualTo(className);
  }

  @Test
  @DisplayName("should use fallback class loader when context class loader is null")
  void shouldUseFallbackClassLoaderWhenContextClassLoaderIsNull() {
    // Given
    Thread currentThread = Thread.currentThread();
    ClassLoader original = currentThread.getContextClassLoader();
    currentThread.setContextClassLoader(null);

    try (MockedStatic<ClassPath> mocked = mockStatic(ClassPath.class)) {
      mocked
          .when(() -> ClassPath.from(any(ClassLoader.class)))
          .thenThrow(new IOException("test fallback"));

      // When / Then
      assertThatThrownBy(
              () -> ReflectionUtils.findAllClasses(ReflectionUtils.class.getPackageName()))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Failed to read classpath");

      mocked.verify(() -> ClassPath.from(ReflectionUtils.class.getClassLoader()), times(1));
    } finally {
      currentThread.setContextClassLoader(original);
    }
  }

  @Test
  @DisplayName("should throw illegal state when missing class name")
  void shouldThrowIllegalStateWhenMissingClassName() {
    // Given
    String missingClassName = "missing.ClassName";
    ClassLoader classLoader = ReflectionUtils.class.getClassLoader();

    // When / Then
    assertThatThrownBy(() -> ReflectionUtils.loadClass(missingClassName, classLoader))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Failed to load class");
  }
}
