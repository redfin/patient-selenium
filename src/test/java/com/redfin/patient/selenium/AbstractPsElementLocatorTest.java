/*
 * Copyright: (c) 2017 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patient.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redfin.patient.selenium.ElementLocatorTestHelper.getSuccessfulInstance;
import static com.redfin.patient.selenium.ElementLocatorTestHelper.getUnsuccessfulInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@DisplayName("An AbstractPsElementLocator")
final class AbstractPsElementLocatorTest
 implements AbstractPsBaseContract<PsElementLocatorImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsElementLocatorImpl getInstance() {
        return getInstance("description", mock(PsConfigImpl.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public PsElementLocatorImpl getInstance(String description,
                                            PsConfigImpl config) {
        return getInstance(description,
                           config,
                           getDriver(),
                           mock(PatientWait.class),
                           mock(Duration.class),
                           mock(Duration.class),
                           mock(Supplier.class),
                           mock(Predicate.class));
    }

    private PsElementLocatorImpl getInstance(String description,
                                             PsConfigImpl config,
                                             PsDriverImpl driver,
                                             PatientWait wait,
                                             Duration defaultTimeout,
                                             Duration defaultNotPresentTimeout,
                                             Supplier<List<WebElement>> elementSupplier,
                                             Predicate<WebElement> elementFilter) {
        return new PsElementLocatorImpl(description,
                                        config,
                                        driver,
                                        wait,
                                        defaultTimeout,
                                        defaultNotPresentTimeout,
                                        elementSupplier,
                                        elementFilter);
    }

    @SuppressWarnings("unchecked")
    private static PsDriverImpl getDriver() {
        PsDriverImpl driver = mock(PsDriverImpl.class);
        DriverExecutor<WebDriver> driverExecutor = mock(DriverExecutor.class);
        when(driver.withWrappedDriver()).thenReturn(driverExecutor);
        when(driverExecutor.apply(any())).thenReturn(null);
        return driver;
    }

    private static final class ValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ofMillis(500),
                                          Duration.ofMillis(500),
                                          mock(Supplier.class),
                                          mock(Predicate.class)));
        }
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null,
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          null,
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          null,
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          null,
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          null,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ofMillis(-500),
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          null,
                                          mock(Supplier.class),
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ofMillis(-500),
                                          null,
                                          mock(Predicate.class)),
                             Arguments.of("description",
                                          mock(PsConfigImpl.class),
                                          mock(PsDriverImpl.class),
                                          mock(PatientWait.class),
                                          Duration.ZERO,
                                          Duration.ZERO,
                                          mock(Supplier.class),
                                          null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while instantiating")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("returns successfully for valid arguments")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testReturnsSuccessfullyForValidArguments(String description,
                                                      PsConfigImpl config,
                                                      PsDriverImpl driver,
                                                      PatientWait wait,
                                                      Duration defaultTimeout,
                                                      Duration defaultNotPresentTimeout,
                                                      Supplier<List<WebElement>> elementSupplier,
                                                      Predicate<WebElement> elementFilter) {
            try {
                Assertions.assertNotNull(getInstance(description,
                                                     config,
                                                     driver,
                                                     wait,
                                                     defaultTimeout,
                                                     defaultNotPresentTimeout,
                                                     elementSupplier,
                                                     elementFilter),
                                         "Should be able to instantiate an instance with valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have been able to instantiate with valid arguments but caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArgumentsProvider.class)
        void testThrowsForInvalidArguments(String description,
                                           PsConfigImpl config,
                                           PsDriverImpl driver,
                                           PatientWait wait,
                                           Duration defaultTimeout,
                                           Duration defaultNotPresentTimeout,
                                           Supplier<List<WebElement>> elementSupplier,
                                           Predicate<WebElement> elementFilter) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(description,
                                                      config,
                                                      driver,
                                                      wait,
                                                      defaultTimeout,
                                                      defaultNotPresentTimeout,
                                                      elementSupplier,
                                                      elementFilter),
                                    "Should have thrown an exception for invalid arguments.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("returns given values for getters")
        void testGettersReturnGivenValues() {
            String description = "hello";
            PsConfigImpl config = mock(PsConfigImpl.class);
            PsDriverImpl driver = mock(PsDriverImpl.class);
            PatientWait wait = mock(PatientWait.class);
            Duration defaultTimeout = Duration.ofMinutes(4);
            Duration defaultNotPresentTimeout = Duration.ofMillis(500);
            Supplier<List<WebElement>> elementSupplier = () -> null;
            Predicate<WebElement> elementFilter = e -> true;
            PsElementLocatorImpl el = getInstance(description,
                                                  config,
                                                  driver,
                                                  wait,
                                                  defaultTimeout,
                                                  defaultNotPresentTimeout,
                                                  elementSupplier,
                                                  elementFilter);
            Assertions.assertAll(() -> Assertions.assertSame(description,
                                                             el.getDescription(),
                                                             "Should return the given description."),
                                 () -> Assertions.assertSame(config,
                                                             el.getConfig(),
                                                             "Should return the given config."),
                                 () -> Assertions.assertSame(driver,
                                                             el.getDriver(),
                                                             "Should return the given driver."),
                                 () -> Assertions.assertSame(wait,
                                                             el.getWait(),
                                                             "Should return the given wait."),
                                 () -> Assertions.assertSame(defaultTimeout,
                                                             el.getDefaultTimeout(),
                                                             "Should return the given timeout."),
                                 () -> Assertions.assertSame(defaultNotPresentTimeout,
                                                             el.getDefaultNotPresentTimeout(),
                                                             "Should return the given not present timeout."),
                                 () -> Assertions.assertSame(elementSupplier,
                                                             el.getElementSupplier(),
                                                             "Should return the given element supplier."),
                                 () -> Assertions.assertSame(elementFilter,
                                                             el.getElementFilter(),
                                                             "Should return the given element filter."));
        }

        @Nested
        @DisplayName("when atIndex is called")
        final class AtIndexTests {

            @Test
            @DisplayName("it creates the expected object with the given index and the default timeout")
            void testAtIndexReturnsExpectedObject() {
                int index = 2;
                Duration timeout = Duration.ofMinutes(2);
                SpecificPsElementRequest request = getSuccessfulInstance(timeout).atIndex(index);
                Assertions.assertAll(() -> Assertions.assertEquals(index, request.getIndex(), "Expected atIndex request to return requested index."),
                                     () -> Assertions.assertEquals(timeout, request.getDefaultTimeout(), "Expected atIndex request to return the set default timeout."));
            }

            @Test
            @DisplayName("throws an exception for a negative index")
            void testAtIndexWithNegativeIndexThrows() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().atIndex(-1),
                                        "Should throw an exception for a negative index.");
            }
        }

        @Nested
        @DisplayName("when get is called")
        final class GetTests {

            @Test
            @DisplayName("with no arguments it calls atIndex(int) with an index of 0")
            void testNoArgGetCallsAtIndexWithZero() {
                Duration timeout = Duration.ofMinutes(5);
                PsElementLocatorImpl el = spy(getSuccessfulInstance(timeout));
                Assertions.assertNotNull(el.get(),
                                         "Expected to get a non-null element");
                verify(el).atIndex(0);
            }

            @Test
            @DisplayName("with duration argument it calls atIndex(int) with an index of 0")
            void testDurationArgGetCallsAtIndexWithZero() {
                Duration timeout = Duration.ofMinutes(5);
                PsElementLocatorImpl el = spy(getSuccessfulInstance(timeout));
                Assertions.assertNotNull(el.get(Duration.ofMinutes(2)),
                                         "Expected to get a non-null element");
                verify(el).atIndex(0);
            }

            @Test
            @DisplayName("throws an exception for a negative timeout")
            void testGetWithNegativeTimeoutThrows() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().get(Duration.ofMillis(-500)),
                                        "Should throw an exception for a negative timeout.");
            }

            @Test
            @DisplayName("throws an exception for a null timeout")
            void testGetWithNullTimeoutThrows() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().get(null),
                                        "Should throw an exception for a null timeout.");
            }
        }

        @Nested
        @DisplayName("when getAll is called")
        final class GetAllTests {

            @Test
            @DisplayName("with no arguments it calls getAll(Duration) with the default timeout")
            void testNoArgGetAllCallsGetAllWithDefaultTimeout() {
                Duration timeout = Duration.ofMinutes(5);
                PsElementLocatorImpl el = spy(getSuccessfulInstance(timeout));
                Assertions.assertNotNull(el.getAll(),
                                         "Expected to get a non-null list.");
                verify(el).getAll(timeout);
            }

            @Test
            @DisplayName("it should return a list with only matching elements")
            void testGetAllReturnsListWithOnlyMatchingElements() {
                Duration timeout = Duration.ZERO;
                Assertions.assertEquals(2,
                                        getSuccessfulInstance(timeout).getAll(timeout).size(),
                                        "A successful getAll(Duration) should return a list with only matching elements.");
            }

            @Test
            @DisplayName("and no matching elements are found then an empty list should be returned")
            void testGetAllUnsuccessfullyReturnsEmptyList() {
                Duration timeout = Duration.ZERO;
                Assertions.assertTrue(getUnsuccessfulInstance(timeout).getAll(timeout).isEmpty(),
                                      "An unsuccessful getAll(Duration) should return an empty list.");
            }
        }

        @Nested
        @DisplayName("when ifPresent is called")
        final class IfPresentTests {

            @Test
            @DisplayName("with consumer argument it calls ifPresent(Consumer, Duration) with the default timeout")
            void testIfPresentWithConsumerCallsIfPresentWithDefaultTimeout() {
                Duration timeout = Duration.ofMinutes(5);
                PsElementLocatorImpl el = spy(getSuccessfulInstance(timeout));
                Consumer<PsElementImpl> consumer = e -> { };
                el.ifPresent(consumer);
                verify(el).ifPresent(consumer, timeout);
            }

            @Test
            @DisplayName("with a null consumer throws an exception")
            void testIfPresentWithNullConsumerThrowsException() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ofMinutes(5)).ifPresent(null),
                                        "Should throw an exception for ifPresent with a null consumer.");
            }

            @Test
            @DisplayName("with a null consumer throws an exception")
            void testIfPresentWithConsumerAndDurationThrowsExceptionForNullConsumer() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ofMinutes(5)).ifPresent(null, Duration.ofMinutes(5)),
                                        "Should throw an exception for ifPresent with a null consumer.");
            }

            @Test
            @DisplayName("with a null consumer throws an exception")
            void testIfPresentWithConsumerAndDurationThrowsExceptionForNullDuration() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ofMinutes(5)).ifPresent(e -> { }, null),
                                        "Should throw an exception for ifPresent with a null timeout.");
            }

            @Test
            @DisplayName("with a null consumer throws an exception")
            void testIfPresentWithConsumerAndDurationThrowsExceptionForNegativeDuration() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ofMinutes(5)).ifPresent(e -> { }, Duration.ofMillis(-500)),
                                        "Should throw an exception for ifPresent with a negative timeout.");
            }

            @Test
            @DisplayName("when present returns an optional executor with executeOther equal to false")
            void testIfPresentReturnsOptionalExecutorWithProperFlagForSuccessful() {
                OptionalExecutor executor = getSuccessfulInstance(Duration.ZERO).ifPresent(e -> { });
                Assertions.assertNotNull(executor,
                                         "The ifPresent call should always return a non-null optional executor instance.");
                Assertions.assertFalse(executor.isExecuteOther(),
                                       "The optional executor for an unsuccessful attempt should have it's execute other flag set to false.");
            }

            @Test
            @DisplayName("when not present returns an optional executor with executeOther equal to true")
            void testIfPresentReturnsOptionalExecutorWithProperFlagForFalse() {
                OptionalExecutor executor = getUnsuccessfulInstance(Duration.ZERO).ifPresent(e -> { });
                Assertions.assertNotNull(executor,
                                         "The ifPresent call should always return a non-null optional executor instance.");
                Assertions.assertTrue(executor.isExecuteOther(),
                                      "The optional executor for a successful attempt should have it's execute other flag set to true.");
            }

            @Test
            @DisplayName("when present calls the given consumer")
            @SuppressWarnings("unchecked")
            void testIfPresentCallsGivenConsumerForSuccessful() {
                Consumer<PsElementImpl> consumer = mock(Consumer.class);
                getSuccessfulInstance(Duration.ZERO).ifPresent(consumer);
                verify(consumer, times(1)).accept(any());
            }

            @Test
            @DisplayName("when present calls the given consumer")
            @SuppressWarnings("unchecked")
            void testIfPresentDoesNotCallGivenConsumerForUnsuccessful() {
                Consumer<PsElementImpl> consumer = mock(Consumer.class);
                getUnsuccessfulInstance(Duration.ZERO).ifPresent(consumer);
                verify(consumer, times(0)).accept(any());
            }
        }

        @Nested
        @DisplayName("when ifNotPresent is called")
        final class IfNotPresentTests {

            @Test
            @DisplayName("with consumer argument it calls ifNotPresent(Runnable, Duration) with the default not present timeout")
            void testIfNotPresentWithRunnableCallsIfNotPresentWithDefaultNotPresentTimeout() {
                Duration notPresentTimeout = Duration.ofMillis(100);
                PsElementLocatorImpl el = spy(getSuccessfulInstance(Duration.ZERO, notPresentTimeout));
                Runnable runnable = () -> { };
                el.ifNotPresent(runnable);
                verify(el).ifNotPresent(runnable, notPresentTimeout);
            }

            @Test
            @DisplayName("with a null runnable throws an exception")
            void testIfNotPresentWithNullRunnableThrowsException() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ZERO).ifNotPresent(null),
                                        "Should throw an exception for ifNotPresent with a null runnable.");
            }

            @Test
            @DisplayName("with a null runnable throws an exception")
            void testIfNotPresentWithRunnableAndDurationThrowsExceptionForNullRunnable() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ZERO).ifNotPresent(null, Duration.ZERO),
                                        "Should throw an exception for ifNotPresent with a null runnable.");
            }

            @Test
            @DisplayName("with a null runnable throws an exception")
            void testIfNotPresentWithRunnableAndDurationThrowsExceptionForNullDuration() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ZERO).ifNotPresent(() -> { }, null),
                                        "Should throw an exception for ifNotPresent with a null timeout.");
            }

            @Test
            @DisplayName("with a null runnable throws an exception")
            void testIfNotPresentWithRunnableAndDurationThrowsExceptionForNegativeDuration() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getSuccessfulInstance(Duration.ZERO).ifNotPresent(() -> { }, Duration.ofMillis(-500)),
                                        "Should throw an exception for ifNotPresent with a negative timeout.");
            }

            @Test
            @DisplayName("when present returns an optional executor with executeOther equal to false")
            void testIfNotPresentReturnsOptionalExecutorWithProperFlagForSuccessful() {
                OptionalExecutor executor = getSuccessfulInstance(Duration.ZERO).ifNotPresent(() -> { });
                Assertions.assertNotNull(executor,
                                         "The ifNotPresent call should always return a non-null optional executor instance.");
                Assertions.assertTrue(executor.isExecuteOther(),
                                      "The optional executor for a successful attempt should have it's execute other flag set to true.");
            }

            @Test
            @DisplayName("when not present returns an optional executor with executeOther equal to true")
            void testIfNotPresentReturnsOptionalExecutorWithProperFlagForUnsuccessful() {
                OptionalExecutor executor = getUnsuccessfulInstance(Duration.ZERO).ifNotPresent(() -> { });
                Assertions.assertNotNull(executor,
                                         "The ifNotPresent call should always return a non-null optional executor instance.");
                Assertions.assertFalse(executor.isExecuteOther(),
                                       "The optional executor for an unsuccessful attempt should have it's execute other flag set to false.");
            }

            @Test
            @DisplayName("when not present calls the given runnable")
            @SuppressWarnings("unchecked")
            void testIfNotPresentCallsGivenRunnableForUnSuccessful() {
                Runnable runnable = mock(Runnable.class);
                getUnsuccessfulInstance(Duration.ZERO).ifNotPresent(runnable);
                verify(runnable, times(1)).run();
            }

            @Test
            @DisplayName("when present does not call the given runnable")
            @SuppressWarnings("unchecked")
            void testIfNotPresentDoesNotCallsGivenRunnableForUuccessful() {
                Runnable runnable = mock(Runnable.class);
                getSuccessfulInstance(Duration.ZERO, Duration.ZERO).ifNotPresent(runnable);
                verify(runnable, times(0)).run();
            }
        }
    }
}
