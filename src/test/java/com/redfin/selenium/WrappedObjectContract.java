package com.redfin.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A test contract interface for types implementing the {@link WrappedObject} interface.
 *
 * @param <T> the type of the wrapped object.
 * @param <W> the type of the wrapped object implementation under test.
 */
public interface WrappedObjectContract<T, W extends WrappedObject<T>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("throws exception for a null consumer")
    default void testAcceptThrowsForNullConsumer() {
        WrappedObject<T> instance = getInstance();
        Assumptions.assumeTrue(null != instance,
                               "Should not have a null instance");
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> instance.accept(null),
                                "Should throw an exception for a null consumer");
    }

    @Test
    @DisplayName("applies the given wrapped object to the given consumer")
    default void testAcceptAppliesWrappedObjectToGivenConsumer() {
        T object = getObject();
        Assumptions.assumeTrue(null != object,
                               "Should not have a null object");
        WrappedObject<T> instance = getInstance(object);
        Assumptions.assumeTrue(null != instance,
                               "Should not have a null instance");
        AtomicBoolean objectUsed = new AtomicBoolean(false);
        instance.accept(e -> objectUsed.set(e == object));
        Assertions.assertTrue(objectUsed.get(),
                              "The wrapped object should have been applied to the given lambda");
    }

    @Test
    @DisplayName("throws an exception for a null function")
    default void testApplyThrowsForNullFunction() {
        WrappedObject<T> instance = getInstance();
        Assumptions.assumeTrue(null != instance,
                               "Should not have a null instance");
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> instance.apply(null),
                                "Should throw an exception for a null function");
    }

    @Test
    @DisplayName("applies the given wrapped object to the given function")
    default void testApplyAppliesWrappedObjectToGivenConsumer() {
        T object = getObject();
        Assumptions.assumeTrue(null != object,
                               "Should not have a null object");
        WrappedObject<T> instance = getInstance(object);
        Assumptions.assumeTrue(null != instance,
                               "Should not have a null instance");
        AtomicBoolean objectUsed = new AtomicBoolean(false);
        instance.apply(e -> {
            objectUsed.set(e == object);
            return null;
        });
        Assertions.assertTrue(objectUsed.get(),
                              "The wrapped object should have been applied to the given lambda");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return an instance of the type implementing {@link WrappedObject}.
     */
    W getInstance();

    /**
     * @param t the object to be wrapped.
     *
     * @return an instance of the type implementing {@link WrappedObject} that is
     * wrapping the given object t.
     */
    W getInstance(T t);

    /**
     * @return an instance of the type that is being wrapped.
     */
    T getObject();
}
