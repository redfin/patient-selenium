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
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public interface CachingExecutorContract<T, E extends AbstractCachingExecutor<T>>
         extends ExecutorContract<E> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Each call to ths method should return a different instance.
     *
     * @return an object to set as the cache.
     */
    T getObject();

    /**
     * Each call to the supplier's get method should return the same instance.
     *
     * @return a supplier of objects to set as the cache.
     */
    Supplier<T> getObjectSupplier();

    /**
     * @param initialObject        the initial object for the caching executor.
     * @param cachedObjectSupplier the supplier for the objects.
     *
     * @return an instance.
     */
    E getInstance(T initialObject,
                  Supplier<T> cachedObjectSupplier);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("when getObject() is called returns the given initial object")
    default void testReturnsGivenInitialObjectWhenNotNull() {
        T expected = getObject();
        Assertions.assertEquals(expected,
                                getInstance(expected, getObjectSupplier()).getObject(),
                                "The getObject() should return the given initial object when it's non-null.");
    }

    @Test
    @DisplayName("when getObject() is called calls the supplier if the initial object is null")
    default void testCallsSupplierWhenInitialObjectIsNull() {
        Supplier<T> supplier = getObjectSupplier();
        T expected = supplier.get();
        Assertions.assertEquals(expected,
                                getInstance(null, supplier).getObject(),
                                "The getObject() should return the given initial object when it's non-null.");
        verify(supplier, times(2)).get();
    }

    @Test
    @DisplayName("when getObject() is called caches the object from the supplier")
    default void testCachesSupplierSuppliedObject() {
        Supplier<T> supplier = getObjectSupplier();
        T expected = supplier.get();
        E impl = getInstance(null, supplier);
        Assertions.assertEquals(expected,
                                impl.getObject(),
                                "The getObject() should return the given initial object when it's non-null.");
        Assertions.assertEquals(expected,
                                impl.getObject(),
                                "The getObject() should return the same cached value when called repeatedly.");
        verify(supplier, times(2)).get();
    }

    @Test
    @DisplayName("when getObject() is called throws an exception if the supplier returns a null object")
    default void testThrowsExceptionIfSupplierReturnsNull() {
        Assertions.assertThrows(IllegalStateException.class,
                                () -> getInstance(null, () -> null).getObject(),
                                "Should throw an exception if the supplier returns a null value.");
    }

    @Test
    @DisplayName("when setCachedObject(Object) is called returns the new given value")
    default void testGetObjectReturnsNewValue() {
        T expected = getObject();
        Supplier<T> supplier = getObjectSupplier();
        E impl = getInstance(expected, supplier);
        T newValue = getObject();
        impl.setCachedObject(newValue);
        Assertions.assertEquals(newValue,
                                impl.getObject(),
                                "Should return the new value after calling setCachedObject(Object).");
        // The supplier should not have been accessed
        verify(supplier, times(0)).get();
    }

    @Test
    @DisplayName("when setCachedObject(Object) is called if given a null object it causes getObject() to call the supplier")
    default void testSettingNullCausesGetObjectToCallSupplier() {
        T expected = getObject();
        Supplier<T> supplier = getObjectSupplier();
        E impl = getInstance(expected, supplier);
        impl.setCachedObject(null);
        T suppliedValue = supplier.get();
        Assertions.assertEquals(suppliedValue,
                                impl.getObject(),
                                "Should return the next value from the supplier after calling setCachedObject(Object) with null.");
        // The supplier should have been accessed
        verify(supplier, times(2)).get();
    }

    @Test
    @DisplayName("when isCachedObjectNull() is called returns false for non-null cache")
    default void testReturnsFalseForNonNullCache() {
        T expected = getObject();
        Supplier<T> supplier = getObjectSupplier();
        E impl1 = getInstance(expected, supplier);
        E impl2 = getInstance(null, supplier);
        impl2.setCachedObject(getObject());
        Assertions.assertAll(() -> Assertions.assertFalse(impl1.isCachedObjectNull(),
                                                          "Should return false from isCachedObjectNull() when cache is not null."),
                             () -> Assertions.assertFalse(impl2.isCachedObjectNull(),
                                                          "Should return false from isCachedObjectNull() when cache is not null."));
    }

    @Test
    @DisplayName("when isCachedObjectNull() is called returns true for a null cache")
    default void testReturnsTrueForNullCache() {
        T expected = getObject();
        Supplier<T> supplier = getObjectSupplier();
        E impl1 = getInstance(null, supplier);
        E impl2 = getInstance(expected, supplier);
        impl2.setCachedObject(null);
        Assertions.assertAll(() -> Assertions.assertTrue(impl1.isCachedObjectNull(),
                                                         "Should return true from isCachedObjectNull() when cache is null."),
                             () -> Assertions.assertTrue(impl2.isCachedObjectNull(),
                                                         "Should return true from isCachedObjectNull() when cache is null."));
    }
}
