package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.implementation.TestPatientConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.redfin.selenium.TestMocks.getMockFilter;
import static org.mockito.Mockito.mock;

@DisplayName("A PatientSeleniumConfig")
final class PatientSeleniumConfigTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while being instantiated")
    final class ConstructorTest {

        @Test
        @DisplayName("is successful with the default builder")
        void testCanBeCreatedFromDefaultBuilder() {
            Assertions.assertNotNull(TestPatientConfig.builder().build(),
                                     "Should have returned a non-null instance for the default builder");
        }

        @ParameterizedTest
        @ArgumentsSource(ValidConstructorArguments.class)
        @DisplayName("is successful for valid constructor arguments")
        void testCanBeCreatedForValidArguments(Predicate<WebElement> filter,
                                               PatientWait wait,
                                               Duration timeout,
                                               int maxElementActionAttempts,
                                               Class<? extends RuntimeException>[] actionExceptionsToIgnore,
                                               Class<? extends RuntimeException>[] lookupExceptionsToIgnore) {
            Assertions.assertNotNull(TestPatientConfig.builder()
                                                      .withFilter(filter)
                                                      .withWait(wait)
                                                      .withTimeout(timeout)
                                                      .withMaxElemementActionAttempts(maxElementActionAttempts)
                                                      .withIgnoredActionExceptions(actionExceptionsToIgnore)
                                                      .withIgnoredLookupExceptions(lookupExceptionsToIgnore)
                                                      .build(),
                                     "Should have returned successfully from the Builder build() method with valid arguments");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws an exception for invalid constructor arguments")
        void testThrowsForInvalidArguments(Predicate<WebElement> filter,
                                           PatientWait wait,
                                           Duration timeout,
                                           int maxElementActionAttempts,
                                           Class<? extends RuntimeException>[] actionExceptionsToIgnore,
                                           Class<? extends RuntimeException>[] lookupExceptionsToIgnore) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> TestPatientConfig.builder()
                                                           .withFilter(filter)
                                                           .withWait(wait)
                                                           .withTimeout(timeout)
                                                           .withMaxElemementActionAttempts(maxElementActionAttempts)
                                                           .withIgnoredActionExceptions(actionExceptionsToIgnore)
                                                           .withIgnoredLookupExceptions(lookupExceptionsToIgnore)
                                                           .build(),
                                    "Should have thrown an exception from Builder build() with invalid arguments");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTest {

        @Test
        @DisplayName("returns the given values from the getter methods")
        @SuppressWarnings("unchecked")
        void testGettersReturnExpectedValues() {
            Predicate<WebElement> filter = mock(Predicate.class);
            PatientWait wait = mock(PatientWait.class);
            Duration timeout = mock(Duration.class);
            int maxElementActionAttempts = 10;
            TestPatientConfig config = TestPatientConfig.builder()
                                                        .withFilter(filter)
                                                        .withWait(wait)
                                                        .withTimeout(timeout)
                                                        .withMaxElemementActionAttempts(maxElementActionAttempts)
                                                        .build();
            Assertions.assertAll(() -> Assertions.assertSame(filter, config.getDefaultFilter(), "Should return the given filter"),
                                 () -> Assertions.assertSame(wait, config.getDefaultWait(), "Should return the given wait"),
                                 () -> Assertions.assertSame(timeout, config.getDefaultTimeout(), "Should return the given timeout"),
                                 () -> Assertions.assertEquals(maxElementActionAttempts, config.getMaxElementActionAttempts(), "Should return the given max action attempts int"));
        }

        @Test
        @DisplayName("returns the expected response to isIgnoredLookupException(Class)")
        void testReturnsExpectedValueForIgnoredLookupClasses() {
            @SuppressWarnings("unchecked")
            Class<? extends RuntimeException>[] exceptions = new Class[2];
            exceptions[0] = IllegalStateException.class;
            exceptions[1] = WebDriverException.class;
            TestPatientConfig config = TestPatientConfig.builder()
                                                        .withIgnoredLookupExceptions(exceptions)
                                                        .build();
            for (Class<? extends RuntimeException> clazz : exceptions) {
                Assertions.assertTrue(config.isIgnoredLookupException(clazz),
                                      "Should return true for expected classes to isIgnoredLookupException(Class)");
            }
            Assertions.assertFalse(config.isIgnoredLookupException(IllegalArgumentException.class),
                                   "Should return false for non-expected classes to isIgnoredLookupException(Class)");
        }

        @Test
        @DisplayName("returns the expected response to isIgnoredActionException(Class)")
        void testReturnsExpectedValueForIgnoredActionClasses() {
            @SuppressWarnings("unchecked")
            Class<? extends RuntimeException>[] exceptions = new Class[2];
            exceptions[0] = IllegalStateException.class;
            exceptions[1] = WebDriverException.class;
            TestPatientConfig config = TestPatientConfig.builder()
                                                        .withIgnoredActionExceptions(exceptions)
                                                        .build();
            for (Class<? extends RuntimeException> clazz : exceptions) {
                Assertions.assertTrue(config.isIgnoredActionException(clazz),
                                      "Should return true for expected classes to isIgnoredActionException(Class)");
            }
            Assertions.assertFalse(config.isIgnoredActionException(IllegalArgumentException.class),
                                   "Should return false for non-expected classes to isIgnoredActionException(Class)");
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static Class<? extends RuntimeException>[] getExceptionArray(Class<? extends RuntimeException>... classes) {
        if (null == classes || classes.length == 0) {
            return (Class<? extends RuntimeException>[]) new Class[2];
        }
        return Arrays.copyOf(classes, classes.length);
    }

    private static final class ValidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(getMockFilter(), mock(PatientWait.class), Duration.ZERO, 4, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 1, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(WebDriverException.class), getExceptionArray()),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, null, getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(WebDriverException.class), null));
        }
    }

    private static final class InvalidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), null, mock(Duration.class), 4, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), null, 4, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), Duration.ofMillis(-1), 4, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 0, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), -1, getExceptionArray(WebDriverException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(NoSuchElementException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(StaleElementReferenceException.class), getExceptionArray(WebDriverException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(WebDriverException.class), getExceptionArray(NoSuchElementException.class)),
                             Arguments.of(getMockFilter(), mock(PatientWait.class), mock(Duration.class), 4, getExceptionArray(WebDriverException.class), getExceptionArray(StaleElementReferenceException.class)));
        }
    }
}
