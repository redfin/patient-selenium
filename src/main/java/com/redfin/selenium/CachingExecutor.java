package com.redfin.selenium;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An interface defining the ability to execute {@link Consumer} and
 * {@link Function} arguments with a cached object.
 *
 * @param <T> the type of the cached object.
 */
public interface CachingExecutor<T> {

    /**
     * Execute the given {@link Consumer} with the cached object.
     *
     * @param consumer the {@link Consumer} to execute with the wrapped object.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    void accept(Consumer<T> consumer);

    /**
     * Execute the given {@link Function} with the cached object.
     *
     * @param function the {@link Function} to execute with the cached object.
     *                 May not be null.
     * @param <R>      the type of the return value from the given function.
     *
     * @return the value returned from the given function when executed with the cached object.
     *
     * @throws IllegalArgumentException if function is null.
     */
    <R> R apply(Function<T, R> function);

    /**
     * Sets the internal cache to the given object.
     *
     * @param element the object to put in the cache.
     *                May be null.
     */
    void setCache(T element);

    /**
     * Clears the internal cache, if any.
     */
    void clearCache();
}
