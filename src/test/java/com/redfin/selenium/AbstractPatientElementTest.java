package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.contracts.FindsElementsTestContract;
import com.redfin.selenium.contracts.WrappedExecutorTestContract;
import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redfin.selenium.TestMocks.getMockConfig;
import static com.redfin.selenium.TestMocks.getMockElementSupplier;
import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPatientElement")
final class AbstractPatientElementTest {

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
                                               Supplier<Optional<WebElement>> elementSupplier,
                                               PatientWait wait,
                                               Duration timeout) {
            Assertions.assertNotNull(getInstance(config, description, elementSupplier, wait, timeout),
                                     "Should be able to instantiate with valid arguments for the constructor");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws an exception when the constructor is called with invalid arguments")
        void testConstructedWithInvalidArguments(TestPatientConfig config,
                                                 String description,
                                                 Supplier<Optional<WebElement>> elementSupplier,
                                                 PatientWait wait,
                                                 Duration timeout) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(config, description, elementSupplier, wait, timeout),
                                    "Should throw an exception with invalid arguments for the constructor");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTest {

        @Nested
        @DisplayName("when isPresent() is called")
        final class IsPresentTest {

            @Test
            @DisplayName("returns true for a present element")
            void testReturnsTrueForPresentElement() {
                Supplier<Optional<WebElement>> supplier = () -> Optional.of(mock(WebElement.class));
                Duration timeout = Duration.ZERO;
                Assertions.assertTrue(getInstance(supplier, timeout).isPresent(),
                                      "Should return true for a present element");
            }

            @Test
            @DisplayName("does not retry once a present element is found")
            void testReturnsTrueImmediatelyForPresentElement() {
                AtomicInteger counter = new AtomicInteger(0);
                Supplier<Optional<WebElement>> supplier = () -> {
                    int currentCount = counter.incrementAndGet();
                    if (currentCount < 2) {
                        return Optional.empty();
                    } else if (currentCount == 2) {
                        return Optional.of(mock(WebElement.class));
                    } else {
                        throw new IllegalStateException("Should have stopped polling after getting a successful result");
                    }
                };
                Duration timeout = Duration.ofSeconds(2);
                Assertions.assertAll(() -> Assertions.assertTrue(getInstance(supplier, timeout).isPresent(),
                                                                 "Should return true for a present element"),
                                     () -> Assertions.assertEquals(2,
                                                                   counter.get(),
                                                                   "Should have stop retrying once an element was found"));
            }

            @Test
            @DisplayName("returns false for a non present element after timeout")
            void testReturnsFalseForNonPresentElement() {
                AtomicInteger counter = new AtomicInteger(0);
                Supplier<Optional<WebElement>> supplier = () -> {
                    counter.getAndIncrement();
                    return Optional.empty();
                };
                Duration timeout = Duration.ofMillis(100);
                Assertions.assertAll(() -> Assertions.assertFalse(getInstance(supplier, timeout).isPresent(),
                                                                  "Should return false for a non-present element"),
                                     () -> Assertions.assertTrue(counter.get() > 1,
                                                                 "Should have called the supplier more than once"));
            }

            @Test
            @DisplayName("clears cache for a non-present element")
            void testClearsCacheWhenNotPresent() {
                Supplier<Optional<WebElement>> supplier = Optional::empty;
                Duration timeout = Duration.ZERO;
                TestPatientElement instance = getInstance(supplier, timeout);
                instance.setCachedElement(mock(WebElement.class));
                instance.isPresent();
                Assertions.assertNull(instance.getCachedElement(),
                                      "An unsuccessful isPresent() check should have cleared the cache");
            }

            @Test
            @DisplayName("resets the internal cache to the newly located element")
            void testSetsCacheToNewElement() {
                WebElement element = mock(WebElement.class);
                Supplier<Optional<WebElement>> supplier = () -> Optional.of(element);
                Duration timeout = Duration.ZERO;
                TestPatientElement instance = getInstance(supplier, timeout);
                instance.setCachedElement(mock(WebElement.class));
                instance.isPresent();
                Assertions.assertSame(element,
                                      instance.getCachedElement(),
                                      "A successful isPresent() check should have set the cache");
            }
        }

        @Nested
        @DisplayName("when isAbsent(Duration) is called")
        final class IsAbsentTest {

            @Test
            @DisplayName("returns true for a non-present element")
            void testReturnsTrueForNonPresentElement() {
                Assertions.assertTrue(getInstance(Optional::empty, mock(Duration.class)).isAbsent(Duration.ZERO),
                                      "Should return true for a non-present element");
            }

            @Test
            @DisplayName("returns false for a present element")
            void testReturnsFalseForPresentElement() {
                Assertions.assertFalse(getInstance(() -> Optional.of(mock(WebElement.class)), mock(Duration.class)).isAbsent(Duration.ZERO),
                                       "Should return false for a present element");
            }

            @Test
            @DisplayName("does not retry once element is not found")
            void testReturnsTrueImmediatelyWhenElementIsNotFound() {
                AtomicInteger counter = new AtomicInteger(0);
                Supplier<Optional<WebElement>> supplier = () -> {
                    int currentCount = counter.incrementAndGet();
                    if (currentCount < 2) {
                        return Optional.of(mock(WebElement.class));
                    } else if (currentCount == 2) {
                        return Optional.empty();
                    } else {
                        throw new IllegalStateException("Should have stopped polling after getting a non-successful result");
                    }
                };
                Duration timeout = Duration.ofSeconds(2);
                Assertions.assertTrue(getInstance(supplier, mock(Duration.class)).isAbsent(timeout),
                                      "Should return true for a non-present element");
            }

            @Test
            @DisplayName("returns false for a non present element after timeout")
            void testReturnsFalseForNonPresentElement() {
                AtomicInteger counter = new AtomicInteger(0);
                Supplier<Optional<WebElement>> supplier = () -> {
                    counter.getAndIncrement();
                    return Optional.of(mock(WebElement.class));
                };
                Duration timeout = Duration.ofMillis(100);
                Assertions.assertAll(() -> Assertions.assertFalse(getInstance(supplier, mock(Duration.class)).isAbsent(timeout),
                                                                  "Should return false for a present element"),
                                     () -> Assertions.assertTrue(counter.get() > 1,
                                                                 "Should have called the supplier more than once"));
            }

            @Test
            @DisplayName("clears the cache for a non-present element")
            void testClearsCacheForNonPresentElement() {
                Supplier<Optional<WebElement>> supplier = Optional::empty;
                Duration timeout = Duration.ZERO;
                TestPatientElement instance = getInstance(supplier, timeout);
                instance.setCachedElement(mock(WebElement.class));
                instance.isAbsent(timeout);
                Assertions.assertNull(instance.getCachedElement(),
                                      "A successful isAbsent(Duration) check should have cleared the cache");
            }

            @Test
            @DisplayName("resets the cache to the newly located element")
            void testResetsCacheForPresentElement() {
                WebElement element = mock(WebElement.class);
                Supplier<Optional<WebElement>> supplier = () -> Optional.of(element);
                Duration timeout = Duration.ZERO;
                TestPatientElement instance = getInstance(supplier, timeout);
                instance.setCachedElement(mock(WebElement.class));
                instance.isAbsent(timeout);
                Assertions.assertSame(element,
                                      instance.getCachedElement(),
                                      "An unsuccessful isAbsent(Duration) check should have set the cache");
            }
        }

        @Test
        @DisplayName("getter methods return given values")
        void testGettersReturnExpectedValues() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBazDescription";
            Supplier<Optional<WebElement>> elementSupplier = getMockElementSupplier();
            PatientWait wait = mock(PatientWait.class);
            Duration timeout = mock(Duration.class);
            WebElement cachedElement = mock(WebElement.class);
            TestPatientElement instance = getInstance(config, description, elementSupplier, wait, timeout);
            instance.setCachedElement(cachedElement);
            // Test the instance's getter values
            Assertions.assertAll(() -> Assertions.assertSame(config, instance.getConfig(), "Should return the given config"),
                                 () -> Assertions.assertSame(description, instance.getDescription(), "Should return the given description"),
                                 () -> Assertions.assertSame(description, instance.toString(), "Should return given description for toString()"),
                                 () -> Assertions.assertSame(elementSupplier, instance.getElementSupplier(), "Should return the given element supplier"),
                                 () -> Assertions.assertSame(wait, instance.getWait(), "Should return the given wait"),
                                 () -> Assertions.assertSame(timeout, instance.getTimeout(), "Should return the given timeout"),
                                 () -> Assertions.assertSame(cachedElement, instance.getCachedElement(), "Should return the given cached element"));
        }

        @Nested
        @DisplayName("when executing commands on the wrapped element")
        final class ExecuteTest {

            @Test
            @DisplayName("looks up an element if the cache is null")
            void testLooksUpElementWhenCacheIsNull() {
                WebElement element = mock(WebElement.class);
                AtomicInteger counter = new AtomicInteger(0);
                Supplier<Optional<WebElement>> supplier = () -> {
                    int currentCount = counter.getAndIncrement();
                    if (currentCount < 2) {
                        return Optional.empty();
                    } else if (currentCount == 2) {
                        return Optional.of(element);
                    } else {
                        throw new IllegalStateException("Should stop looking up elements once successful.");
                    }
                };
                TestPatientElement instance = getInstance(supplier, Duration.ofSeconds(5));
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "Should start with a null internal cache");
                WebElement located = instance.apply(e -> e);
                Assertions.assertSame(element, located, "The expected element should have been located");
            }

            @Test
            @DisplayName("throws an exception if the cache is null and the element can't be looked up")
            void testThrowsExceptionIfCannotLookUpElement() {
                Assertions.assertThrows(NoSuchElementException.class,
                                        () -> getInstance(Optional::empty, Duration.ZERO).apply(e -> e),
                                        "Should have thrown an exception when unable to look up element");
            }

            @Test
            @DisplayName("throws non ignored action exceptions")
            void testThrowsNonIgnoredExceptions() {
                Supplier<Optional<WebElement>> supplier = () -> Optional.of(mock(WebElement.class));
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(supplier, Duration.ZERO).apply(e -> {
                                            throw new IllegalArgumentException("whoops");
                                        }),
                                        "Should throw non-ignored action exceptions");
            }

            @Test
            @DisplayName("keeps retrying on ignored exceptions up to max execution attempts")
            void testIgnoresIgnoredActionExceptionsUpToMaxExecutionAttempts() {
                int maxAttempts = 3;
                TestPatientConfig config = TestPatientConfig.builder()
                                                            .withMaxElemementActionAttempts(maxAttempts)
                                                            .withIgnoredActionExceptions(IllegalStateException.class)
                                                            .build();
                AtomicInteger lookupCounter = new AtomicInteger(0);
                TestPatientElement instance = getInstance(config,
                                                          "fooBarBazDescription",
                                                          () -> {
                                                              lookupCounter.incrementAndGet();
                                                              return Optional.of(mock(WebElement.class));
                                                          },
                                                          PatientWait.builder().build(),
                                                          Duration.ZERO);
                AtomicInteger executionCounter = new AtomicInteger(0);
                Assertions.assertThrows(IllegalStateException.class,
                                        () -> instance.apply(e -> {
                                            executionCounter.incrementAndGet();
                                            throw new IllegalStateException("whoops");
                                        }),
                                        "Should have thrown the exception after the max number of attempts");
                Assertions.assertAll(() -> Assertions.assertEquals(maxAttempts, lookupCounter.get(), "Failed executions should have triggered new element locations"),
                                     () -> Assertions.assertEquals(maxAttempts, executionCounter.get(), "Should have executed the set number of times"));
            }
        }
    }

    @Nested
    @DisplayName("as a WrappedExecutor")
    final class AsWrappedExecutorTest implements WrappedExecutorTestContract<WebElement, TestPatientElement> {

        @Override
        public Class<WebElement> getWrappedObjectClass() {
            return WebElement.class;
        }

        @Override
        public TestPatientElement getExecutor(WebElement wrappedObject) {
            TestPatientConfig config = TestPatientConfig.builder().build();
            TestPatientElement instance = getInstance(config,
                                                      "fooBarBaz",
                                                      getMockElementSupplier(),
                                                      config.getDefaultWait(),
                                                      Duration.ZERO);
            instance.setCachedElement(wrappedObject);
            return instance;
        }
    }

    @Nested
    @DisplayName("as a FindsElements")
    final class AsFindsElementTest implements FindsElementsTestContract<TestPatientElement> {

        @Override
        public TestPatientElement getFindsElementsInstance() {
            return getInstance();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestPatientElement getInstance() {
        return getInstance(getMockConfig(),
                           "DefaultDescription",
                           getMockElementSupplier(),
                           mock(PatientWait.class),
                           mock(Duration.class));
    }

    private static TestPatientElement getInstance(Supplier<Optional<WebElement>> elementSupplier,
                                                  Duration timeout) {
        return getInstance(getMockConfig(),
                           "DefaultDescription",
                           elementSupplier,
                           PatientWait.builder().build(),
                           timeout);
    }

    private static TestPatientElement getInstance(TestPatientConfig config,
                                                  String description,
                                                  Supplier<Optional<WebElement>> elementSupplier,
                                                  PatientWait wait,
                                                  Duration timeout) {
        return new TestPatientElement(config,
                                      description,
                                      elementSupplier,
                                      wait,
                                      timeout);
    }

    private static final class ValidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(getMockConfig(), "h", getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "hello", getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "hello", getMockElementSupplier(), mock(PatientWait.class), Duration.ZERO));
        }
    }

    private static final class InvalidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, "hello", getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "", getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), null, getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "hello", null, mock(PatientWait.class), Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "hello", getMockElementSupplier(), null, Duration.ofMillis(500)),
                             Arguments.of(getMockConfig(), "hello", getMockElementSupplier(), mock(PatientWait.class), null),
                             Arguments.of(getMockConfig(), "hello", getMockElementSupplier(), mock(PatientWait.class), Duration.ofMillis(-1)));
        }
    }
}
