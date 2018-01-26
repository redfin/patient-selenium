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
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@DisplayName("A DefaultDriverExecutor")
final class DefaultDriverExecutorTest
 implements CachingExecutorContract<WebDriver, DefaultDriverExecutor<WebDriver>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public WebDriver getObject() {
        return mock(WebDriver.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Supplier<WebDriver> getObjectSupplier() {
        WebDriver driver = getObject();
        Supplier<WebDriver> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(driver);
        return supplier;
    }

    @Override
    public DefaultDriverExecutor<WebDriver> getInstance() {
        return new DefaultDriverExecutor<>(getObjectSupplier());
    }

    @Override
    public DefaultDriverExecutor<WebDriver> getInstance(WebDriver initialObject,
                                                        Supplier<WebDriver> cachedObjectSupplier) {
        return new DefaultDriverExecutor<>(initialObject, cachedObjectSupplier);
    }

    private static void testConstruction(Supplier<DefaultDriverExecutor<WebDriver>> supplier) {
        try {
            Assertions.assertNotNull(supplier.get(),
                                     "Should have received a non-null instance.");
        } catch (Throwable thrown) {
            Assertions.fail("Should have been able to instantiate but caught: " + thrown);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while instantiating")
    final class ConstructorTests {

        @Test
        @DisplayName("should return successfully for a null initial object")
        void testSuccessfulWithNullInitialObject() {
            testConstruction(() -> new DefaultDriverExecutor<>(null, () -> mock(WebDriver.class)));
        }

        @Test
        @DisplayName("should return successfully for a non-null initial object")
        void testSuccessfulWithInitialObject() {
            testConstruction(() -> new DefaultDriverExecutor<>(mock(WebDriver.class), () -> mock(WebDriver.class)));
        }

        @Test
        @DisplayName("should throw an exception for single argument constructor for a null supplier")
        void testSingleArgConstructorThrowsForNullSupplier() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new DefaultDriverExecutor<>(null),
                                    "Should throw an exception for a null driver supplier.");
        }

        @Test
        @DisplayName("should throw an exception for two argument constructor for a null supplier")
        void testTwoArgConstructorThrowsForNullSupplier() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new DefaultDriverExecutor<>(null, null),
                                    "Should throw an exception for a null driver supplier.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Nested
        @DisplayName("when close() is called")
        final class CloseTests {

            @Test
            @DisplayName("calls close() on the cached driver")
            void testCloseCallsCloseOnDriver() {
                WebDriver driver = mock(WebDriver.class);
                when(driver.getWindowHandles()).thenReturn(Collections.singleton(""));
                getInstance(driver, () -> driver).close();
                verify(driver, times(1)).close();
            }

            @Test
            @DisplayName("clears the cache if there is only a single window handle")
            void testCloseWithSingleHandleClearsTheCache() {
                WebDriver driver = mock(WebDriver.class);
                when(driver.getWindowHandles()).thenReturn(Collections.singleton(""));
                DefaultDriverExecutor<WebDriver> executor = getInstance(driver, () -> driver);
                executor.close();
                Assertions.assertTrue(executor.isCachedObjectNull(),
                                      "Calling close should clear the cache.");
            }

            @Test
            @DisplayName("does not clear the cache if there are more than one handles")
            void testCloseWithMultipleHandlesDoesNotClearsTheCache() {
                WebDriver driver = mock(WebDriver.class);
                Set<String> set = new HashSet<>();
                set.add("");
                set.add("hello");
                when(driver.getWindowHandles()).thenReturn(set);
                DefaultDriverExecutor<WebDriver> executor = getInstance(driver, () -> driver);
                executor.close();
                Assertions.assertFalse(executor.isCachedObjectNull(),
                                      "Calling close should not clear the cache if there are multiple window handles.");
            }

            @Test
            @DisplayName("it does not set the cache from the supplier")
            @SuppressWarnings("unchecked")
            void testCloseOnNullCacheDoesNotSetCache() {
                Supplier<WebDriver> supplier = mock(Supplier.class);
                when(supplier.get()).thenReturn(mock(WebDriver.class));
                DefaultDriverExecutor<WebDriver> executor = getInstance(null, supplier);
                executor.close();
                Assertions.assertTrue(executor.isCachedObjectNull(),
                                      "Calling close should clear the cache.");
                verify(supplier, times(0)).get();
            }
        }

        @Nested
        @DisplayName("when quit() is called")
        final class QuitTests {

            @Test
            @DisplayName("calls close() on the cached driver")
            void testQuitCallsQuitOnDriver() {
                WebDriver driver = mock(WebDriver.class);
                getInstance(driver, () -> driver).quit();
                verify(driver, times(1)).quit();
            }

            @Test
            @DisplayName("clears the cache")
            void testQuitClearsTheCache() {
                WebDriver driver = mock(WebDriver.class);
                DefaultDriverExecutor<WebDriver> executor = getInstance(driver, () -> driver);
                executor.quit();
                Assertions.assertTrue(executor.isCachedObjectNull(),
                                      "Calling close should clear the cache.");
            }

            @Test
            @DisplayName("it does not set the cache from the supplier")
            @SuppressWarnings("unchecked")
            void testQuitOnNullCacheDoesNotSetCache() {
                Supplier<WebDriver> supplier = mock(Supplier.class);
                when(supplier.get()).thenReturn(mock(WebDriver.class));
                DefaultDriverExecutor<WebDriver> executor = getInstance(null, supplier);
                executor.quit();
                Assertions.assertTrue(executor.isCachedObjectNull(),
                                      "Calling close should clear the cache.");
                verify(supplier, times(0)).get();
            }

        }
    }
}
