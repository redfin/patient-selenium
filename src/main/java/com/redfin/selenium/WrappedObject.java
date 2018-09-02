package com.redfin.selenium;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An interface defining the ability to execute {@link Consumer} and
 * {@link Function} arguments with a wrapped object.
 *
 * @param <T> the type of the wrapped object.
 */
public interface WrappedObject<T> {

    /**
     * Execute the given {@link Consumer} with the wrapped object.
     *
     * @param consumer the {@link Consumer} to execute with the wrapped object.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    void accept(Consumer<T> consumer);

    /**
     * Execute the given {@link Function} with the wrapped object.
     *
     * @param function the {@link Function} to execute with the wrapped object.
     *                 May not be null.
     * @param <R>      the type of the return value from the given function.
     *
     * @return the value returned from the given function when executed with the wrapped object.
     *
     * @throws IllegalArgumentException if function is null.
     */
    <R> R apply(Function<T, R> function);
}
