package com.redfin.selenium.contracts;

import com.redfin.selenium.WrappedExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.mock;

/**
 * A test contract for types that implement the {@link WrappedExecutor}
 * interface.
 *
 * @param <T> the type of wrapped object.
 * @param <E> the type of the implementing class under test.
 */
public interface WrappedExecutorTestContract<T, E extends WrappedExecutor<T>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("applies the wrapped object as the argument to the given consumer")
    default void testAcceptGivesWrappedObjectToConsumer() {
        T element = mock(getWrappedObjectClass());
        AtomicBoolean sameElement = new AtomicBoolean(false);
        getExecutor(element).accept(e -> sameElement.set(e == element));
        Assertions.assertTrue(sameElement.get(),
                              "The given wrapped element should have been applied to the given consumer");
    }

    @Test
    @DisplayName("throws an exception for a null consumer argument to accept(Consumer)")
    default void testAcceptThrowsForNullConsumer() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getExecutor(mock(getWrappedObjectClass())).accept(null),
                                "An exception should have been thrown for calling accept(Consumer) with a null argument");
    }

    @Test
    @DisplayName("applies the wrapped object as the argument to the given function")
    default void testApplyGivesWrappedObjectToFunction() {
        T element = mock(getWrappedObjectClass());
        boolean result = getExecutor(element).apply(e -> e == element);
        Assertions.assertTrue(result,
                              "The given wrapped element should have been applied to the given function");
    }

    @Test
    @DisplayName("throws an exception for a null function argument to apply(Function)")
    default void testApplyThrowsForNullFunction() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getExecutor(mock(getWrappedObjectClass())).apply(null),
                                "An exception should have been thrown for calling apply(Function) with a null argument");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the class object of the wrapped object type.
     */
    Class<T> getWrappedObjectClass();

    /**
     * @param wrappedObject the object to be wrapped by the returned executor.
     *                      May be null.
     *
     * @return an instance of the wrapped executor being tested with the
     * wrappedObject argument as the object to be wrapped by the executor.
     */
    E getExecutor(T wrappedObject);
}
