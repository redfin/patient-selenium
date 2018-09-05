package com.redfin.selenium;

import com.redfin.selenium.contract.CachingExecutorContract;
import com.redfin.selenium.contract.ElementContract;
import com.redfin.selenium.extensions.TestElementParameterResolver;
import com.redfin.selenium.extensions.WebElementParameterResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

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
    @DisplayName("while being instantiated")
    final class ConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(ValidConstructorArguments.class)
        @DisplayName("is successful with valid constructor arguments")
        void testCanBeConstructedWithValidConstructorArguments(String description,
                                                               int maxExecutionAttempts,
                                                               Function<Duration, Boolean> checkIfAbsentFunction,
                                                               Supplier<Optional<WebElement>> elementSupplier,
                                                               WebElement initialElement) {
            Assertions.assertNotNull(getInstance(description,
                                                 maxExecutionAttempts,
                                                 checkIfAbsentFunction,
                                                 elementSupplier,
                                                 initialElement),
                                     "Should be able to instantiate an element with valid constructor arguments");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws an exception for invalid constructor arguments")
        void testThrowsExceptionForInvalidConstructorArguments(String description,
                                                               int maxExecutionAttempts,
                                                               Function<Duration, Boolean> checkIfAbsentFunction,
                                                               Supplier<Optional<WebElement>> elementSupplier,
                                                               WebElement initialElement) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(description,
                                                      maxExecutionAttempts,
                                                      checkIfAbsentFunction,
                                                      elementSupplier,
                                                      initialElement),
                                    "Should throw an exception for invalid constructor arguments");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class AbstractElementBehaviorTests {

        @Nested
        @DisplayName("the getters")
        final class GetterBehavior {

            @Test
            @DisplayName("return the given description")
            void testReturnsGivenDescription() {
                String description = "foo";
                Assertions.assertSame(description,
                                      getInstance(description).getDescription(),
                                      "Should return the given description");
            }

            @Test
            @DisplayName("return the given max execution attempts")
            void testReturnsGivenMaxExecutionAttempts() {
                int maxExecutionAttempts = 100;
                Assertions.assertEquals(maxExecutionAttempts,
                                        getInstance(maxExecutionAttempts).getMaxExecutionAttempts(),
                                        "Should return the given max execution attempts");
            }

            @Test
            @DisplayName("return the given check if absent function")
            void testReturnsGivenCheckIfAbsentFunction() {
                Function<Duration, Boolean> function = duration -> true;
                Assertions.assertSame(function,
                                      getInstance(function).getCheckIfAbsentFunction(),
                                      "Should return the given check if absent function");
            }

            @Test
            @DisplayName("return the given element supplier")
            void testReturnsGivenElementSupplier() {
                Supplier<Optional<WebElement>> elementSupplier = Optional::empty;
                Assertions.assertSame(elementSupplier,
                                      getInstance(elementSupplier).getElementSupplier(),
                                      "Should return the given element supplier");
            }

            @Test
            @DisplayName("return the given cached element")
            void testReturnsGivenCachedElement() {
                WebElement element = mock(WebElement.class);
                Assertions.assertSame(element,
                                      getInstance(element).getCachedElement(),
                                      "Should return the given cached element");
            }
        }

        @Test
        @DisplayName("return the given description")
        void testToStringReturnsDescription() {
            String description = "foo";
            Assertions.assertSame(description,
                                  getInstance(description).toString(),
                                  "Should return the given description for toString()");
        }

        @Test
        @DisplayName("accept(Consumer) retrieves an element from the element supplier if the cache is empty")
        void testAcceptRetrievesElementForCacheIfEmpty() {
            WebElement element = mock(WebElement.class);
            TestElement instance = getInstance("description",
                                               1,
                                               d -> false,
                                               () -> Optional.of(element),
                                               null);
            Assumptions.assumeTrue(null == instance.getCachedElement(),
                                   "The instance should not have an element in the cache yet");
            instance.accept(e -> {
            });
            Assertions.assertSame(element,
                                  instance.getCachedElement(),
                                  "Should have retrieved an element from the element supplier");
        }

        @Test
        @DisplayName("accept(Consumer) throws a NoSuchElementException if the supplier returns an empty optional")
        void testAcceptThrowsNoSuchElementExceptionIfCannotGetElement() {
            TestElement instance = getInstance("description",
                                               1,
                                               d -> false,
                                               Optional::empty,
                                               null);
            Assumptions.assumeTrue(null == instance.getCachedElement(),
                                   "The instance should not have an element in the cache yet");
            Assertions.assertThrows(NoSuchElementException.class,
                                    () -> instance.accept(e -> {
                                    }),
                                    "The accept() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException");
        }

        @Test
        @DisplayName("accept(Consumer) does not retry up to the maxExecutionAttempts for a NoSuchElementException")
        void testAcceptDoesNotRetryOnNoSuchElementExceptions() {
            AtomicInteger counter = new AtomicInteger(0);
            TestElement instance = getInstance("description",
                                               10,
                                               d -> false,
                                               Optional::empty,
                                               null);
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
        @DisplayName("accept(Consumer) retries up to the maxExecutionAttempts times for a WebDriverException")
        void testAcceptRetriesUpToMaxExecutionAttemptsForWebDriverExceptions(int maxExecutionAttempts) {
            AtomicInteger counter = new AtomicInteger(0);
            TestElement instance = getInstance("description",
                                               maxExecutionAttempts,
                                               d -> false,
                                               () -> Optional.of(mock(WebElement.class)),
                                               null);
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
        @DisplayName("apply(Function) retrieves an element from the element supplier if the cache is empty")
        void testApplyRetrievesElementForCacheIfEmpty() {
            WebElement element = mock(WebElement.class);
            TestElement instance = getInstance("description",
                                               1,
                                               d -> false,
                                               () -> Optional.of(element),
                                               null);
            Assumptions.assumeTrue(null == instance.getCachedElement(),
                                   "The instance should not have an element in the cache yet");
            instance.apply(e -> null);
            Assertions.assertSame(element,
                                  instance.getCachedElement(),
                                  "Should have retrieved an element from the element supplier");
        }

        @Test
        @DisplayName("apply(Function) throws a NoSuchElementException if the supplier returns an empty optional")
        void testApplyThrowsNoSuchElementExceptionIfCannotGetElement() {
            TestElement instance = getInstance("description",
                                               1,
                                               d -> false,
                                               Optional::empty,
                                               null);
            Assumptions.assumeTrue(null == instance.getCachedElement(),
                                   "The instance should not have an element in the cache yet");
            Assertions.assertThrows(NoSuchElementException.class,
                                    () -> instance.apply(e -> null),
                                    "The apply() call on an element that gets an empty optional from the supplier should throw a NoSuchElementException");
        }

        @Test
        @DisplayName("apply(Function) does not retry up to the maxExecutionAttempts for a NoSuchElementException")
        void testApplyDoesNotRetryOnNoSuchElementExceptions() {
            AtomicInteger counter = new AtomicInteger(0);
            TestElement instance = getInstance("description",
                                               10,
                                               d -> false,
                                               Optional::empty,
                                               null);
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
        @DisplayName("apply(Function) retries up to the maxExecutionAttempts times for a WebDriverException")
        void testApplyRetriesUpToMaxExecutionAttemptsForWebDriverExceptions(int maxExecutionAttempts) {
            AtomicInteger counter = new AtomicInteger(0);
            TestElement instance = getInstance("description",
                                               maxExecutionAttempts,
                                               d -> false,
                                               () -> Optional.of(mock(WebElement.class)),
                                               null);
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
    }

    @Nested
    @DisplayName("as an CachingExecutor")
    @ExtendWith({TestElementParameterResolver.class,
                 WebElementParameterResolver.class})
    final class AsCachingExecutor implements CachingExecutorContract<TestElement, WebElement> {

        @Override
        public WebElement getCachedObjectFromExecutor(TestElement instance) {
            return instance.getCachedElement();
        }
    }

    @Nested
    @DisplayName("as an Element")
    @ExtendWith({TestElementParameterResolver.class,
                 WebElementParameterResolver.class})
    final class AsElement implements ElementContract<TestElement, WebElement> {

        @Override
        public WebElement getCachedObjectFromExecutor(TestElement instance) {
            return instance.getCachedElement();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @SuppressWarnings("unchecked")
    private static TestElement getInstance(String description) {
        return getInstance(description,
                           1,
                           mock(Function.class),
                           mock(Supplier.class),
                           null);
    }

    @SuppressWarnings("unchecked")
    private static TestElement getInstance(int maxExecutionAttempts) {
        return getInstance("defaultString",
                           maxExecutionAttempts,
                           mock(Function.class),
                           mock(Supplier.class),
                           null);
    }

    @SuppressWarnings("unchecked")
    private static TestElement getInstance(Function<Duration, Boolean> checkIfAbsentFunction) {
        return getInstance("defaultString",
                           1,
                           checkIfAbsentFunction,
                           mock(Supplier.class),
                           null);
    }

    @SuppressWarnings("unchecked")
    private static TestElement getInstance(Supplier<Optional<WebElement>> supplier) {
        return getInstance("defaultString",
                           1,
                           mock(Function.class),
                           supplier,
                           null);
    }

    @SuppressWarnings("unchecked")
    private static TestElement getInstance(WebElement initialElement) {
        return getInstance("defaultString",
                           1,
                           mock(Function.class),
                           mock(Supplier.class),
                           initialElement);
    }

    private static TestElement getInstance(String description,
                                           int maxExecutionAttempts,
                                           Function<Duration, Boolean> checkIfAbsentFunction,
                                           Supplier<Optional<WebElement>> elementSupplier,
                                           WebElement initialElement) {
        return new TestElement(description,
                               maxExecutionAttempts,
                               checkIfAbsentFunction,
                               elementSupplier,
                               initialElement);
    }

    private static Function<Duration, Boolean> getIsAbsentFunction(boolean isAbsent) {
        return duration -> isAbsent;
    }

    private static Supplier<Optional<WebElement>> getElementSupplier(boolean isPresent) {
        return () -> isPresent ? Optional.of(mock(WebElement.class)) : Optional.empty();
    }

    private static final class ValidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("a", 1, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("😀", 1, getIsAbsentFunction(false), getElementSupplier(true), mock(WebElement.class)),
                             Arguments.of("hello", Integer.MAX_VALUE, getIsAbsentFunction(false), getElementSupplier(true), mock(WebElement.class)));
        }
    }

    private static final class InvalidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, 1, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("", 1, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("hello", 0, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("hello", -1, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("hello", Integer.MIN_VALUE, getIsAbsentFunction(true), getElementSupplier(false), null),
                             Arguments.of("hello", 1, null, getElementSupplier(false), null),
                             Arguments.of("hello", 1, getIsAbsentFunction(true), null, null));
        }
    }
}
