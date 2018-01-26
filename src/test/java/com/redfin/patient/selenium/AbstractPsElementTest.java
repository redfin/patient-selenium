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

import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPsElement")
final class AbstractPsElementTest
 implements AbstractPsBaseContract<PsElementImpl>,
            FindsElementsContract<PsElementImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsElementImpl getInstance() {
        return new PsElementImpl("hello");
    }

    @Override
    public PsElementImpl getInstance(String description,
                                     PsConfigImpl config) {
        return getInstance(description, config, mock(PsDriverImpl.class), getExecutor());
    }

    private PsElementImpl getInstance(String description,
                                      PsConfigImpl config,
                                      PsDriverImpl driver,
                                      ElementExecutor<WebElement> elementExecutor) {
        return new PsElementImpl(description, config, driver, elementExecutor);
    }

    @SuppressWarnings("unchecked")
    private static ElementExecutor<WebElement> getExecutor() {
        return mock(ElementExecutor.class);
    }

    private static final class ValidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("description", mock(PsConfigImpl.class), mock(PsDriverImpl.class), getExecutor()));
        }
    }

    private static final class InvalidArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("", mock(PsConfigImpl.class), mock(PsDriverImpl.class), getExecutor()),
                             Arguments.of(null, mock(PsConfigImpl.class), mock(PsDriverImpl.class), getExecutor()),
                             Arguments.of("description", null, mock(PsDriverImpl.class), getExecutor()),
                             Arguments.of("description", mock(PsConfigImpl.class), null, getExecutor()),
                             Arguments.of("description", mock(PsConfigImpl.class), mock(PsDriverImpl.class), null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when instantiating")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("returns successfully for valid arguments")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testReturnsSuccessfullyForValidArguments(String description,
                                                      PsConfigImpl config,
                                                      PsDriverImpl driver,
                                                      ElementExecutor<WebElement> elementExecutor) {
            try {
                Assertions.assertNotNull(getInstance(description, config, driver, elementExecutor),
                                         "Should have returned a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have successfully instantiated an instance but caught throwable: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArgumentsProvider.class)
        void testThrowsExceptionForInvalidArguments(String description,
                                                    PsConfigImpl config,
                                                    PsDriverImpl driver,
                                                    ElementExecutor<WebElement> elementExecutor) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(description, config, driver, elementExecutor),
                                    "Should throw an exception for invalid arguments.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("returns the given driver")
        void testReturnsGivenDriver() {
            PsDriverImpl driver = mock(PsDriverImpl.class);
            Assertions.assertSame(driver,
                                  getInstance("description", mock(PsConfigImpl.class), driver, getExecutor()).getDriver(),
                                  "Should return the given driver.");
        }

        @Test
        @DisplayName("returns the given element executor")
        void testReturnsGivenElementExecutor() {
            ElementExecutor<WebElement> executor = getExecutor();
            Assertions.assertSame(executor,
                                  getInstance("description", mock(PsConfigImpl.class), mock(PsDriverImpl.class), executor).withWrappedElement(),
                                  "Should return the given element executor.");
        }
    }
}
