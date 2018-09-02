package com.redfin.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractElement")
final class AbstractElementTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when constructed via the lazily located constructor")
    final class LazilyLocatedConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(InvalidLazilyLocatedArguments.class)
        @DisplayName("throws expected exception for invalid arguments")
        void testThrowsForInvalidArguments(String description,
                                           int maxExecutionAttempts,
                                           Function<Duration, Boolean> checkIfAbsentFunction,
                                           Supplier<Optional<WebElement>> elementSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new TestElement(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier),
                                    "Should have thrown an exception for an invalid argument");
        }

        @ParameterizedTest
        @ArgumentsSource(ValidLazilyLocatedArguments.class)
        @DisplayName("returns the given values")
        void testReturnsGivenValues(String description,
                                    int maxExecutionAttempts,
                                    Function<Duration, Boolean> checkIfAbsentFunction,
                                    Supplier<Optional<WebElement>> elementSupplier) {
            TestElement element = new TestElement(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier);
            Assertions.assertAll(() -> Assertions.assertNotNull(element, "Should have been able to instantiate with valid arguments"),
                                 () -> Assertions.assertEquals(description, element.getDescription(), "Should return the given description"),
                                 () -> Assertions.assertEquals(maxExecutionAttempts, element.getMaxExecutionAttempts(), "Should return the given max execution attempts"),
                                 () -> Assertions.assertEquals(checkIfAbsentFunction, element.getCheckIfAbsentFunction(), "Should return the given check if absent function"),
                                 () -> Assertions.assertEquals(elementSupplier, element.getElementSupplier(), "Should return the given element supplier"),
                                 () -> Assertions.assertNull(element.getCachedElement(), "Should have a null element cache"));
        }
    }

    @Nested
    @DisplayName("when constructed via the already located constructor")
    final class AlreadyLocatedConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(InvalidAlreadyLocatedArguments.class)
        @DisplayName("throws expected exception for invalid arguments")
        void testThrowsForInvalidArguments(String description,
                                           int maxExecutionAttempts,
                                           Function<Duration, Boolean> checkIfAbsentFunction,
                                           Supplier<Optional<WebElement>> elementSupplier,
                                           WebElement initialElement) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new TestElement(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier, initialElement),
                                    "Should have thrown an exception for an invalid argument");
        }

        @ParameterizedTest
        @ArgumentsSource(ValidAlreadyLocatedArguments.class)
        @DisplayName("returns the given values")
        void testReturnsGivenValues(String description,
                                    int maxExecutionAttempts,
                                    Function<Duration, Boolean> checkIfAbsentFunction,
                                    Supplier<Optional<WebElement>> elementSupplier,
                                    WebElement initialElement) {
            TestElement element = new TestElement(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier, initialElement);
            Assertions.assertAll(() -> Assertions.assertNotNull(element, "Should have been able to instantiate with valid arguments"),
                                 () -> Assertions.assertEquals(description, element.getDescription(), "Should return the given description"),
                                 () -> Assertions.assertEquals(maxExecutionAttempts, element.getMaxExecutionAttempts(), "Should return the given max execution attempts"),
                                 () -> Assertions.assertEquals(checkIfAbsentFunction, element.getCheckIfAbsentFunction(), "Should return the given check if absent function"),
                                 () -> Assertions.assertEquals(elementSupplier, element.getElementSupplier(), "Should return the given element supplier"),
                                 // use assert same since we want to validate the exact object reference in the cache
                                 () -> Assertions.assertSame(initialElement, element.getCachedElement(), "Should return the given element cache"));
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Nested
        @DisplayName("when isPresent() is called")
        final class IsPresentTests {

            @ParameterizedTest
            @ValueSource(strings = {"true", "false"})
            @DisplayName("it returns the expected result")
            void testIsPresentReturnsExpectedValue(@StringToBoolean boolean isPresent) {
                Assertions.assertEquals(isPresent,
                                        getInstance(isPresent).isPresent(),
                                        "The isPresent() method on the element should return the expected value");
            }

            @Test
            @DisplayName("it does not use the initial cached value")
            void testIsPresentDoesNotUseCachedValue() {
                Assertions.assertFalse(getInstance(false, mock(WebElement.class)).isPresent(),
                                       "The isPresent() call shouldn't rely on the initial cached element");
            }

            @Test
            @DisplayName("it clears any existing cached element")
            void testIsPresentClearsCache() {
                WebElement element = mock(WebElement.class);
                TestElement instance = getInstance(true, element);
                Assumptions.assumeTrue(element == instance.getCachedElement(),
                                       "Should start with the given cached element");
                instance.isPresent();
                Assertions.assertAll(() -> Assertions.assertNotNull(instance.getCachedElement(), "An isPresent wrapped object should replace the cached element found"),
                                     () -> Assertions.assertNotSame(element, instance.getCachedElement(), "The isPresent call should have cleared the initial element from the cache"));
            }
        }

        @Nested
        @DisplayName("when isAbsent(Duration) is called")
        final class IsAbsentTests {

            @ParameterizedTest
            @ArgumentsSource(ValidIsAbsentDurationsArguments.class)
            @DisplayName("it returns the expected value for valid durations")
            void testIsAbsentReturnsExpectedValue(Duration timeout,
                                                  boolean isPresent) {
                Assertions.assertEquals(isPresent,
                                        !getInstance(isPresent).isAbsent(timeout),
                                        "The isAbsent(Duration) call should return the expected value");
            }

            @ParameterizedTest
            @ArgumentsSource(InvalidIsAbsentDurationsArguments.class)
            @DisplayName("it throws for invalid durations")
            void testIsAbsentThrowsForInvalidDurations(Duration timeout) {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(false).isAbsent(timeout),
                                        "The isAbsent(Duration) call should throw for invalid durations");
            }

            @Test
            @DisplayName("it does not use the initial cached value")
            void testIsAbsentDoesNotUseCachedValue() {
                Assertions.assertTrue(getInstance(false, mock(WebElement.class)).isAbsent(Duration.ZERO),
                                      "The isAbsent(Duration) call shouldn't rely on the initial cached element");
            }

            @Test
            @DisplayName("it clears any existing cached element")
            void testIsAbsentClearsCache() {
                WebElement element = mock(WebElement.class);
                TestElement instance = getInstance(true, element);
                Assumptions.assumeTrue(element == instance.getCachedElement(),
                                       "Should start with the given cached element");
                instance.isAbsent(Duration.ZERO);
                Assertions.assertNull(instance.getCachedElement(),
                                      "The isAbsent(Duration) call should have cleared the initial element from the cache");
            }
        }

        @ParameterizedTest
        @ArgumentsSource(SetCachedElementArguments.class)
        @DisplayName("the setCachedElement method sets the cached element")
        void testSetCacheMethodWorks(WebElement element) {
            TestElement instance = getInstance(true, null);
            Assumptions.assumeTrue(null == instance.getCachedElement(),
                                   "Should start with a null cache");
            instance.setCachedElement(element);
            Assertions.assertSame(element,
                                  instance.getCachedElement(),
                                  "The setCachedElement should return the given object");
        }

        @Test
        @DisplayName("the clearCache methods clears the cache")
        void testClearCacheMethodWorks() {
            TestElement instance = getInstance(true, mock(WebElement.class));
            Assumptions.assumeTrue(null != instance.getCachedElement(),
                                   "Should start with a non-null cache");
            instance.clearCachedElement();
            Assertions.assertNull(instance.getCachedElement(),
                                  "The clearCachedElement method should clear out the cache");
        }

        @Test
        @DisplayName("returns the given description for toString()")
        void testToStringReturnsDescription() {
            String description = "Hello, world";
            TestElement element = new TestElement(description,
                                                  1,
                                                  d -> true,
                                                  () -> Optional.of(mock(WebElement.class)));
            Assertions.assertEquals(description,
                                    element.toString(),
                                    "The toString() method should return the given description");
        }

        @Nested
        @DisplayName("implements the WrappedObject interface and")
        final class WrappedObjectTests
                implements WrappedObjectContract<WebElement, TestElement> {

            @Test
            @DisplayName("accept retrieves an element from the element supplier if the cache is empty")
            void testAcceptRetrievesElementForCacheIfEmpty() {
                WebElement element = mock(WebElement.class);
                TestElement instance = new TestElement("description",
                                                       1,
                                                       d -> false,
                                                       () -> Optional.of(element));
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                instance.accept(e -> { });
                Assertions.assertSame(element,
                                      instance.getCachedElement(),
                                      "Should have retrieved an element from the element supplier");
            }

            @Test
            @DisplayName("accept throws a NoSuchElementException if the supplier returns an empty optional")
            void testAcceptThrowsNoSuchElementExceptionIfCannotGetElement() {
                TestElement instance = new TestElement("description",
                                                       1,
                                                       d -> false,
                                                       Optional::empty);
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertThrows(NoSuchElementException.class,
                                        () -> instance.accept(e -> { }),
                                        "The accept() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException");
            }

            @Test
            @DisplayName("accept does not retry up to the maxExecutionAttempts for a NoSuchElementException")
            void testAcceptDoesNotRetryOnNoSuchElementExceptions() {
                AtomicInteger counter = new AtomicInteger(0);
                TestElement instance = new TestElement("description",
                                                       10,
                                                       d -> false,
                                                       Optional::empty);
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertAll(() -> Assertions.assertThrows(NoSuchElementException.class,
                                                                   () -> instance.accept(e -> counter.incrementAndGet()),
                                                                   "The accept() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException"),
                                     () -> Assertions.assertEquals(0,
                                                                   counter.get(),
                                                                   "The execution should not have been retried for a no such element exception"));
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 10})
            @DisplayName("accept retries up to the maxExecutionAttempts times for a WebDriverException")
            void testAcceptRetriesUpToMaxExecutionAttemptsForWebDriverExceptions(int maxExecutionAttempts) {
                AtomicInteger counter = new AtomicInteger(0);
                TestElement instance = new TestElement("description",
                                                       maxExecutionAttempts,
                                                       d -> false,
                                                       () -> Optional.of(mock(WebElement.class)));
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertAll(() -> Assertions.assertThrows(WebDriverException.class,
                                                                   () -> instance.accept(e -> {
                                                                       counter.incrementAndGet();
                                                                       throw new WebDriverException("whoops");
                                                                   }),
                                                                   "The accept() call on an element that throws a WebDriverException should propagate that exception"),
                                     () -> Assertions.assertEquals(maxExecutionAttempts,
                                                                   counter.get(),
                                                                   "The execution should have retried for maxExecutionAttempts"));
            }

            @Test
            @DisplayName("apply retrieves an element from the element supplier if the cache is empty")
            void testApplyRetrievesElementForCacheIfEmpty() {
                WebElement element = mock(WebElement.class);
                TestElement instance = new TestElement("description",
                                                       1,
                                                       d -> false,
                                                       () -> Optional.of(element));
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                instance.apply(e -> null);
                Assertions.assertSame(element,
                                      instance.getCachedElement(),
                                      "Should have retrieved an element from the element supplier");
            }

            @Test
            @DisplayName("apply throws a NoSuchElementException if the supplier returns an empty optional")
            void testApplyThrowsNoSuchElementExceptionIfCannotGetElement() {
                TestElement instance = new TestElement("description",
                                                       1,
                                                       d -> false,
                                                       Optional::empty);
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertThrows(NoSuchElementException.class,
                                        () -> instance.apply(e -> null),
                                        "The apply() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException");
            }

            @Test
            @DisplayName("apply does not retry up to the maxExecutionAttempts for a NoSuchElementException")
            void testApplyDoesNotRetryOnNoSuchElementExceptions() {
                AtomicInteger counter = new AtomicInteger(0);
                TestElement instance = new TestElement("description",
                                                       10,
                                                       d -> false,
                                                       Optional::empty);
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertAll(() -> Assertions.assertThrows(NoSuchElementException.class,
                                                                   () -> instance.apply(e -> counter.incrementAndGet()),
                                                                   "The apply() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException"),
                                     () -> Assertions.assertEquals(0,
                                                                   counter.get(),
                                                                   "The execution should not have been retried for a no such element exception"));
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 10})
            @DisplayName("apply retries up to the maxExecutionAttempts times for a WebDriverException")
            void testApplyRetriesUpToMaxExecutionAttemptsForWebDriverExceptions(int maxExecutionAttempts) {
                AtomicInteger counter = new AtomicInteger(0);
                TestElement instance = new TestElement("description",
                                                       maxExecutionAttempts,
                                                       d -> false,
                                                       () -> Optional.of(mock(WebElement.class)));
                Assumptions.assumeTrue(null == instance.getCachedElement(),
                                       "The instance should not have an element in the cache yet");
                Assertions.assertAll(() -> Assertions.assertThrows(WebDriverException.class,
                                                                   () -> instance.apply(e -> {
                                                                       counter.incrementAndGet();
                                                                       throw new WebDriverException("whoops");
                                                                   }),
                                                                   "The apply() call on an element that throws a WebDriverException should propagate that exception"),
                                     () -> Assertions.assertEquals(maxExecutionAttempts,
                                                                   counter.get(),
                                                                   "The execution should have retried for maxExecutionAttempts"));
            }

            @Override
            public TestElement getInstance() {
                return AbstractElementTest.getInstance(true);
            }

            @Override
            public TestElement getInstance(WebElement webElement) {
                return AbstractElementTest.getInstance(true, webElement);
            }

            @Override
            public WebElement getObject() {
                return mock(WebElement.class);
            }
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestElement getInstance(boolean isPresent) {
        return getInstance(isPresent, mock(WebElement.class));
    }

    private static TestElement getInstance(boolean isPresent,
                                           WebElement initialElement) {
        return new TestElement("default_description",
                               1,
                               duration -> !isPresent,
                               () -> Optional.ofNullable(isPresent ? mock(WebElement.class) : null),
                               initialElement);
    }

    private static final class ValidIsAbsentDurationsArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Duration.ZERO, true),
                             Arguments.of(Duration.ofDays(5), true),
                             Arguments.of(Duration.ZERO, false),
                             Arguments.of(Duration.ofDays(5), false));
        }
    }

    private static final class InvalidIsAbsentDurationsArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of((Duration) null),
                             Arguments.of(Duration.ofMillis(-100)));
        }
    }

    private static final class SetCachedElementArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(mock(WebElement.class)),
                             Arguments.of((WebElement) null));
        }
    }

    private static final class ValidLazilyLocatedArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("😄", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("😄", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty));
        }
    }

    private static final class InvalidLazilyLocatedArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of(null, 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", 0, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", -1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", Integer.MIN_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", 1, null, (Supplier<Optional<WebElement>>) Optional::empty),
                             Arguments.of("hello", 1, (Function<Duration, Boolean>) duration -> true, null));
        }
    }

    private static final class ValidAlreadyLocatedArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, null),
                             Arguments.of("😄", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, null),
                             Arguments.of("hello", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, null),
                             Arguments.of("😄", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, null),
                             Arguments.of("hello", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("😄", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("😄", Integer.MAX_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)));
        }
    }

    private static final class InvalidAlreadyLocatedArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("", 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of(null, 1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", 0, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", -1, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", Integer.MIN_VALUE, (Function<Duration, Boolean>) duration -> true, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", 1, null, (Supplier<Optional<WebElement>>) Optional::empty, mock(WebElement.class)),
                             Arguments.of("hello", 1, (Function<Duration, Boolean>) duration -> true, null, mock(WebElement.class)));
        }
    }

    private static final class StringToBooleanArgumentConverter
                    implements ArgumentConverter {

        @Override
        public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
            if (source instanceof String) {
                if ("false".equalsIgnoreCase((String) source)) {
                    return false;
                } else if ("true".equalsIgnoreCase((String) source)) {
                    return true;
                }
            }
            throw new ArgumentConversionException("Unable to convert the given argument");
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @ConvertWith(StringToBooleanArgumentConverter.class)
    private @interface StringToBoolean {}
}
