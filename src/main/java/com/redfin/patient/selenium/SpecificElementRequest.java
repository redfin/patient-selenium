package com.redfin.patient.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Consumer;

public interface SpecificElementRequest<D extends WebDriver,
                                        W extends WebElement,
                                        C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                        P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                        L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                        E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    /**
     * Keep trying to locate the n-th element that matches the set requirements.
     * The index is zero based so the first element would be index 0.
     * If the timeout is reached and no n-th element is found then throw an exception.
     *
     * @return the requested element instance.
     *
     * @throws NoSuchElementException if no matching element is found within the default timeout.
     */
    E get();

    /**
     * Keep trying to locate the n-th element that matches the set requirements.
     * The index is zero based so the first element would be index 0.
     * If the timeout is reached and no n-th element is found then throw an exception.
     *
     * @param timeout the Duration timeout to use for waiting when trying to locate a matching element.
     *                May not be null or negative.
     *
     * @return the requested element instance.
     *
     * @throws IllegalArgumentException if the timeout is null or negative.
     * @throws NoSuchElementException   if no matching element is found within the given timeout.
     */
    E get(Duration timeout);


    /**
     * Check if there are any elements that can be found (e.g. like
     * calling {@link #get()} but without the exception if there isn't
     * anything found. If there are any elements found within the default timeout,
     * then run the given consumer with the first found element.
     * If the timeout is reached and no matching elements have been found then
     * return an {@link OptionalExecutor} that will run any runnable given
     * to {@link OptionalExecutor#orElse(Runnable)}.
     *
     * @param consumer the {@link Consumer} to be executed if there is a matching element found.
     *                 May not be null.
     *
     * @return an {@link OptionalExecutor} for this elements that will run the
     * {@link Runnable} given to {@link OptionalExecutor#orElse(Runnable)} if there
     * are no elements found and the timeout is reached or that won't if any elements are found.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    OptionalExecutor ifPresent(Consumer<E> consumer);

    /**
     * Check if there are any elements that can be found (e.g. like
     * calling {@link #get()} but without the exception if there isn't
     * anything found. If there are any elements found within the given timeout,
     * then run the given consumer with the first found element.
     * If the timeout is reached and no matching elements have been found then
     * return an {@link OptionalExecutor} that will run any runnable given
     * to {@link OptionalExecutor#orElse(Runnable)}.
     *
     * @param consumer the {@link Consumer} to be executed if there is a matching element found.
     *                 May not be null.
     * @param timeout  the {@link Duration} timeout to keep trying to check that there
     *                 are no matching elements.
     *                 Note that a duration of 0 means try to locate an element one.
     *                 May not be null or negative.
     *
     * @return an {@link OptionalExecutor} for this elements that will run the
     * {@link Runnable} given to {@link OptionalExecutor#orElse(Runnable)} if there
     * are no elements found and the timeout is reached or that won't if any elements are found.
     *
     * @throws IllegalArgumentException if consumer is null or if timeout is null or negative.
     */
    OptionalExecutor ifPresent(Consumer<E> consumer,
                               Duration timeout);

    /**
     * Same as calling {@link #ifNotPresent(Runnable, Duration)} with the default
     * timeout.
     *
     * @param runnable the {@link Runnable} to be executed if there are no elements
     *                 found that match.
     *                 May not be null.
     *
     * @return an {@link OptionalExecutor} for this elements that will run the
     * {@link Runnable} given to {@link OptionalExecutor#orElse(Runnable)} if there
     * are still elements found and the timeout is reached or that won't if no elements are found.
     *
     * @throws IllegalArgumentException if runnable is null.
     */
    OptionalExecutor ifNotPresent(Runnable runnable);

    /**
     * Check if there are any elements that can be found (e.g. like
     * calling {@link #get()} but without the exception if there isn't
     * anything found. If there are no elements found within the given timeout,
     * then run the given runnable. If the timeout is reached and
     * elements that match are still found return an OptionalExecutor
     * that will run any runnable given to {@link OptionalExecutor#orElse(Runnable)}.
     *
     * @param runnable the {@link Runnable} to be executed if there are no elements
     *                 found that match.
     *                 May not be null.
     * @param timeout  the {@link Duration} timeout to keep trying to check that there
     *                 are no matching elements.
     *                 Note that a duration of 0 means try to locate an element one.
     *                 May not be null or negative.
     *
     * @return an {@link OptionalExecutor} for this elements that will run the
     * {@link Runnable} given to {@link OptionalExecutor#orElse(Runnable)} if there
     * are still elements found and the timeout is reached or that won't if no elements are found.
     *
     * @throws IllegalArgumentException if consumer is null or if timeout is null or negative.
     */
    OptionalExecutor ifNotPresent(Runnable runnable,
                                  Duration timeout);
}
