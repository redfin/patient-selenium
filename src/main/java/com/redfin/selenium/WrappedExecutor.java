package com.redfin.selenium;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A WrappedExecutor is a type that wraps another object and applies it
 * as the argument to a given consumer or function.
 *
 * @param <T> the type of object that is wrapped.
 */
public interface WrappedExecutor<T> {

    /**
     * Give the wrapped object to the given consumer's
     * {@link Consumer#accept(Object)} method.
     *
     * @param consumer the {@link Consumer} to execute
     *                 with the wrapped object.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    void accept(Consumer<T> consumer);

    /**
     * Give the wrapped object to the given function's
     * {@link Function#apply(Object)} method and return the result.
     *
     * @param function the {@link Function} to execute
     *                 with the wrapped object.
     *                 May not be null.
     * @param <R>      the type returned by the given function.
     *
     * @return the result of applying the wrapped object to the function.
     *
     * @throws IllegalArgumentException if function is null.
     */
    <R> R apply(Function<T, R> function);
}
