package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientElement;
import com.redfin.selenium.implementation.TestPatientElementLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redfin.selenium.TestMocks.getMockConfig;
import static com.redfin.selenium.TestMocks.getMockElementListSupplier;
import static com.redfin.selenium.TestMocks.getMockFilter;
import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPatientElementLocator")
final class AbstractPatientElementLocatorTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while being instantiated")
    final class ConstructorTest {

        @ParameterizedTest
        @ArgumentsSource(ValidConstructorArguments.class)
        @DisplayName("instantiates successfully when the constructor is called with valid arguments")
        void testConstructedWithValidArguments(TestPatientConfig config,
                                               String description,
                                               Supplier<List<WebElement>> elementListSupplier,
                                               PatientWait wait,
                                               Duration timeout,
                                               Predicate<WebElement> filter) {
            Assertions.assertNotNull(getInstance(config, description, elementListSupplier, wait, timeout, filter),
                                     "Should be able to instantiate with valid arguments for the constructor");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws an exception when the constructor is called with invalid arguments")
        void testConstructedWithInvalidArguments(TestPatientConfig config,
                                                 String description,
                                                 Supplier<List<WebElement>> elementListSupplier,
                                                 PatientWait wait,
                                                 Duration timeout,
                                                 Predicate<WebElement> filter) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(config, description, elementListSupplier, wait, timeout, filter),
                                    "Should throw an exception with invalid arguments for the constructor");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTest {

        @Nested
        @DisplayName("when get() or get(int) are called")
        final class GetTest {

            @ParameterizedTest
            @ValueSource(ints = {Integer.MIN_VALUE, -1})
            @DisplayName("throws an exception for a negative index")
            void testGetWithIndexThrowsForNegativeIndex(int index) {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().get(index),
                                        "Should throw an exception for a negative index to get(int)");
            }

            @ParameterizedTest
            @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
            @DisplayName("returns a non-null element instance for a valid index")
            void testGetWithIndexReturnsNonNullInstance(int index) {
                Assertions.assertNotNull(getInstance().get(index),
                                         "Any non-negative index should return a non-null element for get(int)");
            }

            @ParameterizedTest
            @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
            @DisplayName("returns a non-null element instance for a valid index")
            void testGetWithIndexReturnsSameInstance(int index) {
                TestPatientElementLocator instance = getInstance();
                TestPatientElement element = instance.get(index);
                Assertions.assertSame(element,
                                      instance.get(index),
                                      "Repeated calls to get(int) with the same index should return the same instance");
            }

            @Test
            @DisplayName("returns a different element instance for different indices")
            void testGetWithDifferentIndexReturnsDifferentInstances() {
                TestPatientElementLocator instance = getInstance();
                TestPatientElement element = instance.get(1);
                Assertions.assertNotSame(element,
                                         instance.get(2),
                                         "Calls to get(int) with different indices should return a different instance");
            }

            @Test
            @DisplayName("return the same element if the index is 0")
            void testGetWithZeroReturnsSameElementAsGet() {
                TestPatientElementLocator instance = getInstance();
                TestPatientElement element = instance.get(0);
                Assertions.assertSame(element,
                                      instance.get(),
                                      "Calls to get() should return the same instance as a call to get(int) with 0 as the argument");
            }
        }

        @Nested
        @DisplayName("when getAll() is called")
        final class GetAllTest {

            @Test
            @DisplayName("returns a non-null but empty list when no element is found")
            void testReturnsNonNullListWhenNoElementIsFound() {
                List<TestPatientElement> elements = getInstanceWithResultCount(0).getAll();
                Assertions.assertAll(() -> Assertions.assertNotNull(elements, "The returned list from getAll() should never be null"),
                                     () -> Assertions.assertTrue(elements.isEmpty(), "The returned list from getAll() should be empty if no elements are found"));
            }

            @Test
            @DisplayName("returns a non-null but empty list when no element is found")
            void testReturnsListOfExpectedSize() {
                int expectedCount = 3;
                List<TestPatientElement> elements = getInstanceWithResultCount(expectedCount).getAll();
                Assertions.assertAll(() -> Assertions.assertNotNull(elements, "The returned list from getAll() should never be null"),
                                     () -> Assertions.assertEquals(expectedCount, elements.size(), "The returned list from getAll() should have the expected size"));
            }
        }

        @Nested
        @DisplayName("when a clone method is called")
        final class CloneTest {

            @Test
            @DisplayName("with a non-null wait it returns an instance with the expected values")
            void testCloneWithWaitReturnsExpectedInstance() {
                TestPatientConfig config = getMockConfig();
                String description = "fooBarBaz";
                Supplier<List<WebElement>> elementListSupplier = getMockElementListSupplier();
                PatientWait wait = mock(PatientWait.class);
                Duration timeout = mock(Duration.class);
                Predicate<WebElement> filter = getMockFilter();
                TestPatientElementLocator instance = getInstance(config, description, elementListSupplier, wait, timeout, filter);
                // Clone the instance
                PatientWait newWait = mock(PatientWait.class);
                TestPatientElementLocator newInstance = instance.clone(newWait);
                // Validate the result
                Assertions.assertAll(() -> Assertions.assertNotNull(newInstance, "Should not have returned a null instance from clone(PatientWait)"),
                                     () -> Assertions.assertSame(config, newInstance.getConfig(), "Should have returned the original config"),
                                     () -> Assertions.assertSame(description, newInstance.getDescription(), "Should have returned the original description"),
                                     () -> Assertions.assertSame(elementListSupplier, newInstance.getElementListSupplier(), "Should have returned the original supplier"),
                                     () -> Assertions.assertSame(newWait, newInstance.getWait(), "Should have returned the new wait"),
                                     () -> Assertions.assertSame(timeout, newInstance.getTimeout(), "Should have returned the original timeout"),
                                     () -> Assertions.assertSame(filter, newInstance.getFilter(), "Should have returned the original filter"));
            }

            @Test
            @DisplayName("with a null wait it throws an exception")
            void testCloneWithWaitThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().clone((PatientWait) null),
                                        "Should throw an exception for a null wait argument to clone(PatientWait)");
            }

            @Test
            @DisplayName("with a non-null timeout it returns an instance with the expected values")
            void testCloneWithTimeoutReturnsExpectedInstance() {
                TestPatientConfig config = getMockConfig();
                String description = "fooBarBaz";
                Supplier<List<WebElement>> elementListSupplier = getMockElementListSupplier();
                PatientWait wait = mock(PatientWait.class);
                Duration timeout = mock(Duration.class);
                Predicate<WebElement> filter = getMockFilter();
                TestPatientElementLocator instance = getInstance(config, description, elementListSupplier, wait, timeout, filter);
                // Clone the instance
                Duration newTimeout = mock(Duration.class);
                TestPatientElementLocator newInstance = instance.clone(newTimeout);
                // Validate the result
                Assertions.assertAll(() -> Assertions.assertNotNull(newInstance, "Should not have returned a null instance from clone(PatientWait)"),
                                     () -> Assertions.assertSame(config, newInstance.getConfig(), "Should have returned the original config"),
                                     () -> Assertions.assertSame(description, newInstance.getDescription(), "Should have returned the original description"),
                                     () -> Assertions.assertSame(elementListSupplier, newInstance.getElementListSupplier(), "Should have returned the original supplier"),
                                     () -> Assertions.assertSame(wait, newInstance.getWait(), "Should have returned the original wait"),
                                     () -> Assertions.assertSame(newTimeout, newInstance.getTimeout(), "Should have returned the new timeout"),
                                     () -> Assertions.assertSame(filter, newInstance.getFilter(), "Should have returned the original filter"));
            }

            @Test
            @DisplayName("with a negative timeout it throws an exception")
            void testCloneWithTimeoutThrowsForNegative() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().clone(Duration.ofMillis(-1)),
                                        "Should throw an exception for a negative timeout argument to clone(Duration)");
            }

            @Test
            @DisplayName("with a null timeout it throws an exception")
            void testCloneWithTimeoutThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().clone((Duration) null),
                                        "Should throw an exception for a null timeout argument to clone(Duration)");
            }

            @Test
            @DisplayName("with a non-null filter it returns an instance with the expected values")
            void testCloneWithFilterReturnsExpectedInstance() {
                TestPatientConfig config = getMockConfig();
                String description = "fooBarBaz";
                Supplier<List<WebElement>> elementListSupplier = getMockElementListSupplier();
                PatientWait wait = mock(PatientWait.class);
                Duration timeout = mock(Duration.class);
                Predicate<WebElement> filter = getMockFilter();
                TestPatientElementLocator instance = getInstance(config, description, elementListSupplier, wait, timeout, filter);
                // Clone the instance
                Predicate<WebElement> newFilter = getMockFilter();
                TestPatientElementLocator newInstance = instance.clone(newFilter);
                // Validate the result
                Assertions.assertAll(() -> Assertions.assertNotNull(newInstance, "Should not have returned a null instance from clone(PatientWait)"),
                                     () -> Assertions.assertSame(config, newInstance.getConfig(), "Should have returned the original config"),
                                     () -> Assertions.assertSame(description, newInstance.getDescription(), "Should have returned the original description"),
                                     () -> Assertions.assertSame(elementListSupplier, newInstance.getElementListSupplier(), "Should have returned the original supplier"),
                                     () -> Assertions.assertSame(wait, newInstance.getWait(), "Should have returned the original wait"),
                                     () -> Assertions.assertSame(timeout, newInstance.getTimeout(), "Should have returned the original timeout"),
                                     () -> Assertions.assertSame(newFilter, newInstance.getFilter(), "Should have returned the new filter"));
            }

            @Test
            @DisplayName("with a null filter it throws an exception")
            void testCloneWithFilterThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().clone((Predicate<WebElement>) null),
                                        "Should throw an exception for a null filter argument to clone(Predicate)");
            }
        }

        @Test
        @DisplayName("returns the expected values from the getter methods")
        void testGettersReturnGivenValues() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBaz";
            Supplier<List<WebElement>> elementListSupplier = getMockElementListSupplier();
            PatientWait wait = mock(PatientWait.class);
            Duration timeout = mock(Duration.class);
            Predicate<WebElement> filter = getMockFilter();
            TestPatientElementLocator instance = getInstance(config, description, elementListSupplier, wait, timeout, filter);
            Assertions.assertAll(() -> Assertions.assertSame(config, instance.getConfig(), "Should return the given config"),
                                 () -> Assertions.assertSame(description, instance.getDescription(), "Should return the given description"),
                                 () -> Assertions.assertSame(elementListSupplier, instance.getElementListSupplier(), "Should return the given supplier"),
                                 () -> Assertions.assertSame(wait, instance.getWait(), "Should return the given wait"),
                                 () -> Assertions.assertSame(timeout, instance.getTimeout(), "Should return the given timeout"),
                                 () -> Assertions.assertSame(filter, instance.getFilter(), "Should return the given filter"),
                                 () -> Assertions.assertSame(description, instance.toString(), "Should return the given description for toString()"));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestPatientElementLocator getInstance() {
        return getInstance(getMockConfig(),
                           "defaultDescription",
                           getMockElementListSupplier(),
                           mock(PatientWait.class),
                           Duration.ZERO,
                           getMockFilter());
    }

    private static TestPatientElementLocator getInstanceWithResultCount(int numResults) {
        List<WebElement> list = new ArrayList<>();
        for (int i = 0; i < numResults; i++) {
            list.add(mock(WebElement.class));
        }
        return getInstance(getMockConfig(),
                           "withResultsCount(" + numResults + ")",
                           () -> list,
                           PatientWait.builder().build(),
                           Duration.ZERO,
                           e -> true);
    }

    private static TestPatientElementLocator getInstance(TestPatientConfig config,
                                                         String description,
                                                         Supplier<List<WebElement>> elementListSupplier,
                                                         PatientWait wait,
                                                         Duration timeout,
                                                         Predicate<WebElement> filter) {
        return new TestPatientElementLocator(config,
                                             description,
                                             elementListSupplier,
                                             wait,
                                             timeout,
                                             filter);
    }

    private static final class ValidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(getMockConfig(), "h", getMockElementListSupplier(), mock(PatientWait.class), Duration.ofMillis(1), getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), mock(PatientWait.class), Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), mock(PatientWait.class), Duration.ofDays(5), getMockFilter()));
        }
    }

    private static final class InvalidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, "hello", getMockElementListSupplier(), mock(PatientWait.class), Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), null, getMockElementListSupplier(), mock(PatientWait.class), Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), "", getMockElementListSupplier(), mock(PatientWait.class), Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", null, mock(PatientWait.class), Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), null, Duration.ZERO, getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), mock(PatientWait.class), null, getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), mock(PatientWait.class), Duration.ofMillis(-1), getMockFilter()),
                             Arguments.of(getMockConfig(), "hello", getMockElementListSupplier(), mock(PatientWait.class), Duration.ZERO, null));
        }
    }
}
