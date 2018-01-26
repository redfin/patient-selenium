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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@DisplayName("A DefaultElementExecutor")
final class DefaultElementExecutorTest
 implements CachingExecutorContract<WebElement, DefaultElementExecutor<WebElement>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final int DEFAULT_STALE_ELEMENT_RETRIES = 3;

    @Override
    public WebElement getObject() {
        return staticGetObject();
    }

    @Override
    public Supplier<WebElement> getObjectSupplier() {
        return staticGetObjectSupplier();
    }

    @Override
    public DefaultElementExecutor<WebElement> getInstance() {
        return getInstance(getObject(),
                           getObjectSupplier());
    }

    @Override
    public DefaultElementExecutor<WebElement> getInstance(WebElement initialObject,
                                                          Supplier<WebElement> cachedObjectSupplier) {
        // We need to support null specially for the interface contract
        if (null != initialObject) {
            return new DefaultElementExecutor<>(initialObject, cachedObjectSupplier);
        } else {
            DefaultElementExecutor<WebElement> element = new DefaultElementExecutor<>(mock(WebElement.class), cachedObjectSupplier);
            element.clearCachedElement();
            return element;
        }
    }

    private DefaultElementExecutor<WebElement> getInstance(WebElement initialObject,
                                                           Supplier<WebElement> cachedObjectSupplier,
                                                           int maxStaleRetries) {
        return new DefaultElementExecutor<>(initialObject,
                                            cachedObjectSupplier,
                                            maxStaleRetries);
    }

    private static WebElement staticGetObject() {
        return mock(WebElement.class);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<WebElement> staticGetObjectSupplier() {
        WebElement element = staticGetObject();
        Supplier<WebElement> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(element);
        return supplier;
    }

    private static final class InvalidTwoArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, staticGetObjectSupplier()),
                             Arguments.of(staticGetObject(), null));
        }
    }

    private static final class ValidThreeArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(staticGetObject(), staticGetObjectSupplier(), 1),
                             Arguments.of(staticGetObject(), staticGetObjectSupplier(), Integer.MAX_VALUE));
        }
    }

    private static final class InvalidThreeArgumentsProvider
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, staticGetObjectSupplier(), 1),
                             Arguments.of(staticGetObject(), null, 1),
                             Arguments.of(staticGetObject(), staticGetObjectSupplier(), 0),
                             Arguments.of(staticGetObject(), staticGetObjectSupplier(), -1));
        }
    }

    private static void testConstructor(Supplier<DefaultElementExecutor<WebElement>> supplier) {
        try {
            Assertions.assertNotNull(supplier.get(),
                                     "Should have returned a non-null object from the constructor.");
        } catch (Throwable thrown) {
            Assertions.fail("Should have been able to instantiate but caught throwable: " + thrown);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when instantiating")
    final class ConstructorTests {

        @Test
        @DisplayName("successfully returns for valid arguments to two argument constructor")
        void testReturnsSuccessfullyForValidArguments() {
            testConstructor(DefaultElementExecutorTest.this::getInstance);
        }

        @ParameterizedTest
        @DisplayName("throws for invalid arguments to two argument constructor")
        @ArgumentsSource(InvalidTwoArgumentsProvider.class)
        void testThrowsForInvalidArguments(WebElement initialElement,
                                           Supplier<WebElement> cachedObjectSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new DefaultElementExecutor<>(initialElement, cachedObjectSupplier),
                                    "Should have thrown an exception for invalid arguments.");
        }

        @ParameterizedTest
        @DisplayName("successfully returns for valid arguments to three argument constructor")
        @ArgumentsSource(ValidThreeArgumentsProvider.class)
        void testReturnsSuccessfullyForValidArguments(WebElement initialElement,
                                                      Supplier<WebElement> cachedObjectSupplier,
                                                      int maxStaleRetries) {
            testConstructor(() -> getInstance(initialElement,
                                              cachedObjectSupplier,
                                              maxStaleRetries));
        }

        @ParameterizedTest
        @DisplayName("throws for invalid arguments to two argument constructor")
        @ArgumentsSource(InvalidThreeArgumentsProvider.class)
        void testThrowsForInvalidArguments(WebElement initialElement,
                                           Supplier<WebElement> cachedObjectSupplier,
                                           int maxStaleRetries) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(initialElement, cachedObjectSupplier, maxStaleRetries),
                                    "Should have thrown an exception for invalid arguments.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("should default to the expected max stale element retries")
        void testDefaultsToExpectedMaxStaleRetries() {
            Assertions.assertEquals(DEFAULT_STALE_ELEMENT_RETRIES,
                                    getInstance().getMaxStaleRetries(),
                                    "Should have the expected number of max stale element retries as the default.");
        }

        @ParameterizedTest
        @DisplayName("should return the given max stale element retries")
        @ArgumentsSource(ValidThreeArgumentsProvider.class)
        void testReturnsGivenMaxRetries(WebElement initialElement,
                                        Supplier<WebElement> cachedObjectSupplier,
                                        int maxStaleRetries) {
            Assertions.assertEquals(maxStaleRetries,
                                    getInstance(initialElement, cachedObjectSupplier, maxStaleRetries).getMaxStaleRetries(),
                                    "Should return the given max stale retries.");
        }

        @Test
        @DisplayName("calling clearCachedElement should set the cache to null")
        void testClearCacheSetsCacheToNull() {
            DefaultElementExecutor<WebElement> executor = getInstance();
            Assertions.assertFalse(executor.isCachedObjectNull(),
                                   "Initially the cache should not be null.");
            executor.clearCachedElement();
            Assertions.assertTrue(executor.isCachedObjectNull(),
                                  "Calling clearCachedElement should clear the cached value.");
        }

        @Test
        @DisplayName("should keep retrying when stale element exceptions are caught")
        @SuppressWarnings("unchecked")
        void testApplyRetriesOnStaleElementExceptions() {
            int numRetries = 5;
            WebElement element = mock(WebElement.class);
            when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("whoops"));
            Supplier<WebElement> supplier = mock(Supplier.class);
            when(supplier.get()).thenReturn(element);
            Assertions.assertThrows(StaleElementReferenceException.class,
                                    () -> getInstance(element, supplier, numRetries).apply(WebElement::isDisplayed),
                                    "Expected to have it throw a stale element reference exception.");
            verify(supplier, times(numRetries)).get();
        }
    }
}
