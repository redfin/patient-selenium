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
import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorBuilderImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

public interface AbstractPsBaseContract<T extends AbstractPsBase<WebDriver,
                                                                 WebElement,
                                                                 PsConfigImpl,
                                                                 PsDriverImpl,
                                                                 PsElementLocatorBuilderImpl,
                                                                 PsElementLocatorImpl,
                                                                 PsElementImpl>>
         extends Testable<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param description the String description.
     * @param config      the config to be used.
     *
     * @return an instance of the type under test with the given description and config
     * to be passed to the AbstractPsBase of the instance.
     */
    T getInstance(String description, PsConfigImpl config);

    class ValidBaseArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", new PsConfigImpl()));
        }
    }

    class InvalidBaseArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, new PsConfigImpl()),
                             Arguments.of("", new PsConfigImpl()),
                             Arguments.of("hello", null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @ParameterizedTest
    @DisplayName("can create an AbstractPsBase with valid arguments")
    @ArgumentsSource(ValidBaseArguments.class)
    default void testConstructorReturnsSuccessfullyForValidArguments(String description,
                                                                     PsConfigImpl config) {
        try {
            Assertions.assertNotNull(getInstance(description, config),
                                     "Should have returned a non-null instance for valid arguments.");
        } catch (Throwable thrown) {
            Assertions.fail("Should have been able to create an instance with valid arguments but caught: " + thrown);
        }
    }

    @ParameterizedTest
    @DisplayName("throws an exception when creating an AbstractPsBase with invalid arguments")
    @ArgumentsSource(InvalidBaseArguments.class)
    default void testConstructorThrowsForInvalidArguments(String description,
                                                          PsConfigImpl config) {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance(description, config),
                                "Should throw an exception for invalid arguments.");
    }

    @Test
    @DisplayName("returns the given description")
    default void testReturnsGivenDescription() {
        String description = "hello";
        Assertions.assertEquals(description,
                                getInstance(description, new PsConfigImpl()).getDescription(),
                                "Should return the given description.");
    }

    @Test
    @DisplayName("returns the given config")
    default void testReturnsGivenConfig() {
        PsConfigImpl config = new PsConfigImpl();
        Assertions.assertEquals(config,
                                getInstance("hello", config).getConfig(),
                                "Should return the given config.");

    }

    @Test
    @DisplayName("the base toString() should return the given description")
    default void testToStringReturnsGivenDescription() {
        String description = "hello";
        Assertions.assertEquals(description,
                                getInstance(description, new PsConfigImpl()).toString(),
                                "Should return the given description.");
    }
}
