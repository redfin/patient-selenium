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

import com.redfin.contractual.Testable;
import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.impl.PsConfigImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPsConfig")
final class AbstractPsConfigTest
 implements Testable<PsConfigImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsConfigImpl getInstance() {
        return new PsConfigImpl();
    }

    private PsConfigImpl getInstance(PatientWait defaultWait,
                                     Duration defaultTimeout,
                                     Duration defaultNotPresentTimeout,
                                     Predicate<WebElement> defaultElementFilter,
                                     Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        return new PsConfigImpl(defaultWait,
                                defaultTimeout,
                                defaultNotPresentTimeout,
                                defaultElementFilter,
                                elementNotFoundExceptionBuilderFunction);
    }

    private static final class ValidArgumentsProvider
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(mock(PatientWait.class), Duration.ZERO, Duration.ZERO, mock(Predicate.class), mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), Duration.ofMillis(500), Duration.ofMillis(500), mock(Predicate.class), mock(Function.class)));
        }
    }

    private static final class InvalidArgumentsProvider
            implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, Duration.ZERO, Duration.ZERO, mock(Predicate.class), mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), null, Duration.ZERO, mock(Predicate.class), mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), Duration.ZERO, null, mock(Predicate.class), mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), Duration.ZERO, Duration.ZERO, null, mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), Duration.ZERO, Duration.ZERO, mock(Predicate.class), null),
                             Arguments.of(mock(PatientWait.class), Duration.ofMillis(-500), Duration.ZERO, mock(Predicate.class), mock(Function.class)),
                             Arguments.of(mock(PatientWait.class), Duration.ZERO, Duration.ofMillis(-500), mock(Predicate.class), mock(Function.class)));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while instantiating")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("returns a non-null instance for valid arguments")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testSuccessfulWithValidArguments(PatientWait defaultWait,
                                              Duration defaultTimeout,
                                              Duration defaultNotPresentTimeout,
                                              Predicate<WebElement> defaultElementFilter,
                                              Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
            try {
                Assertions.assertNotNull(getInstance(defaultWait,
                                                     defaultTimeout,
                                                     defaultNotPresentTimeout,
                                                     defaultElementFilter,
                                                     elementNotFoundExceptionBuilderFunction),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have successfully created the config but caught throwable: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArgumentsProvider.class)
        void testThrowsForInvalidArguments(PatientWait defaultWait,
                                              Duration defaultTimeout,
                                              Duration defaultNotPresentTimeout,
                                              Predicate<WebElement> defaultElementFilter,
                                              Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(defaultWait,
                                                      defaultTimeout,
                                                      defaultNotPresentTimeout,
                                                      defaultElementFilter,
                                                      elementNotFoundExceptionBuilderFunction),
                                    "Should throw an exception for invalid arguments.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @ParameterizedTest
        @DisplayName("returns the given values from the getter methods")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testReturnsGivenValues(PatientWait defaultWait,
                                    Duration defaultTimeout,
                                    Duration defaultNotPresentTimeout,
                                    Predicate<WebElement> defaultElementFilter,
                                    Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
            PsConfigImpl config = getInstance(defaultWait,
                                              defaultTimeout,
                                              defaultNotPresentTimeout,
                                              defaultElementFilter,
                                              elementNotFoundExceptionBuilderFunction);
            Assertions.assertAll(() -> Assertions.assertSame(defaultWait,
                                                             config.getDefaultWait(),
                                                             "Should have returned the given wait."),
                                 () -> Assertions.assertSame(defaultTimeout,
                                                             config.getDefaultTimeout(),
                                                             "Should have returned the given timeout."),
                                 () -> Assertions.assertSame(defaultNotPresentTimeout,
                                                             config.getDefaultNotPresentTimeout(),
                                                             "Should have returned the given not present timeout."),
                                 () -> Assertions.assertSame(defaultElementFilter,
                                                             config.getDefaultElementFilter(),
                                                             "Should have returned the given element filter."),
                                 () -> Assertions.assertSame(elementNotFoundExceptionBuilderFunction,
                                                             config.getElementNotFoundExceptionBuilderFunction(),
                                                             "Should have returned the given exception function."));
        }
    }
}
