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
import com.redfin.patient.selenium.impl.PsElementLocatorBuilderImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPsElementLocatorBuilder")
final class AbstractPsElementLocatorBuilderTest
 implements AbstractPsBaseContract<PsElementLocatorBuilderImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final PatientWait DEFAULT_WAIT = PatientWait.builder().build();
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(400);
    private static final Duration DEFAULT_NOT_PRESENT_TIMEOUT = Duration.ofMinutes(5);
    private static final Predicate<WebElement> DEFAULT_ELEMENT_FILTER = e -> true;

    @Override
    public PsElementLocatorBuilderImpl getInstance() {
        return getInstance("description",
                           getConfig());
    }

    @Override
    @SuppressWarnings("unchecked")
    public PsElementLocatorBuilderImpl getInstance(String description,
                                                   PsConfigImpl config) {
        return getInstance(description,
                           config,
                           mock(PsDriverImpl.class),
                           mock(Function.class));
    }

    private PsElementLocatorBuilderImpl getInstance(String description,
                                                    PsConfigImpl config,
                                                    PsDriverImpl driver,
                                                    Function<By, List<WebElement>> baseSeleniumLocatorFunction) {
        return new PsElementLocatorBuilderImpl(description, config, driver, baseSeleniumLocatorFunction);
    }

    private static PsConfigImpl getConfig() {
        return new PsConfigImpl(DEFAULT_WAIT,
                                DEFAULT_TIMEOUT,
                                DEFAULT_NOT_PRESENT_TIMEOUT,
                                DEFAULT_ELEMENT_FILTER,
                                NoSuchElementException::new);
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("", getConfig(), mock(PsDriverImpl.class), mock(Function.class)),
                             Arguments.of(null, getConfig(), mock(PsDriverImpl.class), mock(Function.class)),
                             Arguments.of("description", null, mock(PsDriverImpl.class), mock(Function.class)),
                             Arguments.of("description", getConfig(), null, mock(Function.class)),
                             Arguments.of("description", getConfig(), mock(PsDriverImpl.class), null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @ParameterizedTest
    @DisplayName("throws an exception for invalid constructor arguments")
    @ArgumentsSource(InvalidArgumentsProvider.class)
    void testThrowsForInvalidConstructorArguments(String description,
                                                  PsConfigImpl config,
                                                  PsDriverImpl driver,
                                                  Function<By, List<WebElement>> baseSeleniumLocatorFunction) {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(description, config, driver, baseSeleniumLocatorFunction),
                                "Should throw an exception for an invalid argument.");
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("return the expected values")
        void testGettersReturnExpectedValues() {
            String description = "description";
            PsConfigImpl config = getConfig();
            PsDriverImpl driver = mock(PsDriverImpl.class);
            Function<By, List<WebElement>> function = by -> Collections.emptyList();
            PsElementLocatorBuilderImpl impl = getInstance(description,
                                                           config,
                                                           driver,
                                                           function);
            Assertions.assertAll(() -> Assertions.assertSame(description,
                                                             impl.getDescription(),
                                                             "Should return the given description."),
                                 () -> Assertions.assertSame(config,
                                                             impl.getConfig(),
                                                             "Should return the given config."),
                                 () -> Assertions.assertSame(config.getDefaultWait(),
                                                             impl.getWait(),
                                                             "Should return the given wait."),
                                 () -> Assertions.assertSame(config.getDefaultTimeout(),
                                                             impl.getDefaultTimeout(),
                                                             "Should return the given timeout."),
                                 () -> Assertions.assertSame(config.getDefaultNotPresentTimeout(),
                                                             impl.getDefaultNotPresentTimeout(),
                                                             "Should return the given not present timeout."),
                                 () -> Assertions.assertSame(config.getDefaultElementFilter(),
                                                             impl.getElementFilter(),
                                                             "Should return the given element filter."),
                                 () -> Assertions.assertSame(driver,
                                                             impl.getDriver(),
                                                             "Should return the given driver."),
                                 () -> Assertions.assertSame(function,
                                                             impl.getBaseSeleniumLocatorFunction(),
                                                             "Should return the given function."));
        }

        @Nested
        @DisplayName("when calling withWait(Wait)")
        final class SetWaitTests {

            @Test
            @DisplayName("returns a self reference")
            void testReturnsSelfReference() {
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(impl,
                                      impl.withWait(mock(PatientWait.class)),
                                      "Should return a self reference.");
            }

            @Test
            @DisplayName("can set the wait value")
            void testCanSetWait() {
                PatientWait wait = mock(PatientWait.class);
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(wait,
                                      impl.withWait(wait).getWait(),
                                      "Should return the newly set wait.");
            }

            @Test
            @DisplayName("throws an exception for a null argument")
            void testThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withWait(null),
                                        "Should throw an exception for a null argument.");
            }
        }

        @Nested
        @DisplayName("when calling withTimeout(Duration)")
        final class SetTimeoutTests {

            @Test
            @DisplayName("returns a self reference")
            void testReturnsSelfReference() {
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(impl,
                                      impl.withTimeout(Duration.ZERO),
                                      "Should return a self reference.");
            }

            @Test
            @DisplayName("can set the wait value")
            void testCanSetWait() {
                Duration timeout = mock(Duration.class);
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(timeout,
                                      impl.withTimeout(timeout).getDefaultTimeout(),
                                      "Should return the newly set duration.");
            }

            @Test
            @DisplayName("throws an exception for a null argument")
            void testThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withTimeout(null),
                                        "Should throw an exception for a null argument.");
            }

            @Test
            @DisplayName("throws an exception for a negative argument")
            void testThrowsForNegative() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withTimeout(Duration.ofMillis(-100)),
                                        "Should throw an exception for a negative argument.");
            }
        }

        @Nested
        @DisplayName("when calling withNotPresentTimeout(Duration)")
        final class SetNotPresentTimeoutTests {

            @Test
            @DisplayName("returns a self reference")
            void testReturnsSelfReference() {
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(impl,
                                      impl.withNotPresentTimeout(Duration.ZERO),
                                      "Should return a self reference.");
            }

            @Test
            @DisplayName("can set the wait value")
            void testCanSetWait() {
                Duration timeout = mock(Duration.class);
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(timeout,
                                      impl.withNotPresentTimeout(timeout).getDefaultNotPresentTimeout(),
                                      "Should return the newly set duration.");
            }

            @Test
            @DisplayName("throws an exception for a null argument")
            void testThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withNotPresentTimeout(null),
                                        "Should throw an exception for a null argument.");
            }

            @Test
            @DisplayName("throws an exception for a negative argument")
            void testThrowsForNegative() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withNotPresentTimeout(Duration.ofMillis(-100)),
                                        "Should throw an exception for a negative argument.");
            }
        }

        @Nested
        @DisplayName("when calling withFilter(Predicate)")
        final class SetFilterTests {

            @Test
            @DisplayName("returns a self reference")
            void testReturnsSelfReference() {
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(impl,
                                      impl.withFilter(e -> true),
                                      "Should return a self reference.");
            }

            @Test
            @DisplayName("can set the wait value")
            @SuppressWarnings("unchecked")
            void testCanSetWait() {
                Predicate<WebElement> filter = mock(Predicate.class);
                PsElementLocatorBuilderImpl impl = getInstance();
                Assertions.assertSame(filter,
                                      impl.withFilter(filter).getElementFilter(),
                                      "Should return the newly set filter.");
            }

            @Test
            @DisplayName("throws an exception for a null argument")
            void testThrowsForNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().withFilter(null),
                                        "Should throw an exception for a null argument.");
            }
        }

        @Nested
        @DisplayName("when calling by(By)")
        final class ByTests {

            @Test
            @DisplayName("returns a non-null object for a non-null by")
            void testReturnsNonNull() {
                Assertions.assertNotNull(getInstance().by(By.id("foo")),
                                         "Should return a non-null instance for a non-null by.");
            }

            @Test
            @DisplayName("throws an exception for a null argument")
            void testThrowsForNullBy() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().by(null),
                                        "Should throw an exception for a null argument.");
            }
        }
    }
}
