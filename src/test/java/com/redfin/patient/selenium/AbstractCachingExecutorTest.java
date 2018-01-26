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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("An AbstractCachingExecutor")
final class AbstractCachingExecutorTest
 implements CachingExecutorContract<String, AbstractCachingExecutorTest.Impl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static class Impl
         extends AbstractCachingExecutor<String> {

        private Impl(String initialObject,
                     Supplier<String> cachedObjectSupplier) {
            super(initialObject, cachedObjectSupplier);
        }
    }

    @Override
    public Impl getInstance() {
        return getInstance("initial", () -> "supplied");
    }

    @Override
    public Impl getInstance(String initialObject,
                            Supplier<String> cachedObjectSupplier) {
        return new Impl(initialObject, cachedObjectSupplier);
    }

    @Override
    public String getObject() {
        return "hello";
    }

    @Override
    public Supplier<String> getObjectSupplier() {
        return createMessageSupplier("hello");
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String> createMessageSupplier(String message) {
        Supplier<String> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(message);
        return supplier;
    }

    private static class ValidArgumentsProvider
              implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, createMessageSupplier("hello")),
                             Arguments.of(null, createMessageSupplier("")),
                             Arguments.of(null, createMessageSupplier(null)),
                             Arguments.of("", createMessageSupplier("hello")),
                             Arguments.of("", createMessageSupplier("")),
                             Arguments.of("", createMessageSupplier(null)),
                             Arguments.of("hello", createMessageSupplier("hello")),
                             Arguments.of("hello", createMessageSupplier("")),
                             Arguments.of("hello", createMessageSupplier(null)));
        }
    }

    private static class InvalidArgumentsProvider
              implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", null));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while constructing")
    final class ConstructorTests {

        @ParameterizedTest
        @DisplayName("returns a non-null instance for valid arguments")
        @ArgumentsSource(ValidArgumentsProvider.class)
        void testCanConstructWithValidArguments(String initialObject,
                                                Supplier<String> cachedObjectSupplier) {
            try {
                Assertions.assertNotNull(getInstance(initialObject, cachedObjectSupplier),
                                         "Should have received a non-null instance for valid arguments.");
            } catch (Throwable thrown) {
                Assertions.fail("Should have been able to instantiate with valid arguments but caught: " + thrown);
            }
        }

        @ParameterizedTest
        @DisplayName("throws an exception for invalid arguments")
        @ArgumentsSource(InvalidArgumentsProvider.class)
        void testThrowsForInvalidArguments(String initialObject,
                                           Supplier<String> cachedObjectSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(initialObject, cachedObjectSupplier),
                                    "Should have thrown an exception for invalid arguments.");
        }
    }
}
