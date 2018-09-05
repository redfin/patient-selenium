package com.redfin.selenium.contract;

import com.redfin.selenium.CachingExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A test contract interface for types implementing the {@link CachingExecutor} interface.
 *
 * @param <E> the type of the {@link CachingExecutor} implementation under test.
 * @param <T> the type of the object wrapped by the executor.
 */
public interface CachingExecutorContract<E extends CachingExecutor<T>, T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("returns the given cached object")
    default void testGetCacheReturnsGivenObject(@Cache E instance,
                                                @Cacheable T object) {
        instance.setCache(object);
        Assertions.assertSame(object,
                              getCachedObjectFromExecutor(instance),
                               "The executor should return the given object from the cache");
    }

    @Test
    @DisplayName("returns null for the cached object after the cache is cleared")
    default void testSetCachedObjectSetsTheCache(@Cache E instance,
                                                 @Cacheable T object) {
        instance.setCache(object);
        instance.clearCache();
        Assertions.assertNull(getCachedObjectFromExecutor(instance),
                              "After clearing the cache the cached object should be null");
    }

    @Test
    @DisplayName("throws exception for a null consumer argument to accept(Consumer)")
    default void testAcceptThrowsForNullConsumer(@Cache E instance,
                                                 @Cacheable T object) {
        instance.setCache(object);
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> instance.accept(null),
                                "Should throw an exception for a null consumer");
    }

    @Test
    @DisplayName("applies the given cached object to the consumer argument to accept(Consumer)")
    default void testAcceptAppliesWrappedObjectToGivenConsumer(@Cache E instance,
                                                               @Cacheable T object) {
        instance.setCache(object);
        AtomicBoolean objectUsed = new AtomicBoolean(false);
        instance.accept(e -> objectUsed.set(e == object));
        Assertions.assertTrue(objectUsed.get(),
                              "The cached object should have been applied to the given consumer");
    }

    @Test
    @DisplayName("throws exception for a null function argument to apply(Function)")
    default void testApplyThrowsForNullFunction(@Cache E instance,
                                                @Cacheable T object) {
        instance.setCache(object);
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> instance.apply(null),
                                "Should throw an exception for a null function");
    }

    @Test
    @DisplayName("applies the given cached object to the function argument to apply(Function)")
    default void testApplyAppliesWrappedObjectToGivenFunction(@Cache E instance,
                                                              @Cacheable T object) {
        instance.setCache(object);
        AtomicBoolean objectUsed = new AtomicBoolean(false);
        instance.apply(e -> {
            objectUsed.set(e == object);
            return null;
        });
        Assertions.assertTrue(objectUsed.get(),
                              "The cached object should have been applied to the given function");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param instance the instance of the type implementing {@link CachingExecutor}.
     *                 Should never be null.
     *
     * @return the object currently in the cache of instance.
     */
    T getCachedObjectFromExecutor(E instance);
}
