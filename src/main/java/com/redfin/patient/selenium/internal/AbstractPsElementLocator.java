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

package com.redfin.patient.selenium.internal;

import com.redfin.patience.PatientWait;
import com.redfin.patience.exceptions.PatientTimeoutException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.validate;

/**
 * Base type for an element locator.
 * This is an immutable type that allows you to lazily locate selenium
 * elements.
 * When using directly from a driver this is an intermediate type that is quickly released but
 * is the main type used for page objects.
 *
 * @param <D>    the type of wrapped WebDriver.
 * @param <W>    the type of wrapped WebElement.
 * @param <C>    the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P>    the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B>    the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <THIS> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E>    the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsElementLocator<D extends WebDriver,
                                               W extends WebElement,
                                               C extends AbstractPsConfig<D, W, C, P, B, THIS, E>,
                                               P extends AbstractPsDriver<D, W, C, P, B, THIS, E>,
                                               B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, THIS, E>,
                                            THIS extends AbstractPsElementLocator<D, W, C, P, B, THIS, E>,
                                               E extends AbstractPsElement<D, W, C, P, B, THIS, E>>
              extends AbstractPsBase<D, W, C, P, B, THIS, E> {

    private static final String ELEMENT_FORMAT = "%s.get(%s)";
    private static final String NOT_FOUND_FORMAT = "No element found matching %s.get(%s) within %s after %d attempts";

    private final P driver;
    private final PatientWait wait;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;
    private final Supplier<List<W>> elementSupplier;
    private final Predicate<W> elementFilter;

    /**
     * Create a new AbstractPsElementLocator with the given values.
     *
     * @param description              the string description of the element locator.
     *                                 May not be empty or null.
     * @param config                   the {@link AbstractPsConfig} for this element locator.
     *                                 May not be null.
     * @param driver                   the {@link AbstractPsDriver} for this element locator.
     *                                 May not be null.
     * @param wait                     the {@link PatientWait} for this element locator.
     *                                 may not be null.
     * @param defaultTimeout           the default {@link Duration} timeout when locating an
     *                                 element you expect to be present.
     *                                 May not be null or negative.
     * @param defaultNotPresentTimeout the default {@link Duration} timeout when trying
     *                                 to check that an element is not present that you
     *                                 don't expect to be there.
     *                                 May not be null or negative.
     * @param elementSupplier          the {@link Supplier} of a list of {@link WebElement}s.
     *                                 An exception will be thrown later if it ever returns a null list.
     *                                 May not be null.
     * @param elementFilter            the {@link Predicate} to use to check elements returned from the
     *                                 supplier. Elements that don't pass the predicate won't be considered
     *                                 a valid match.
     *                                 May not be null.
     *
     * @throws IllegalArgumentException if any argument is null, if the description is empty,
     *                                  or if defaultTimeout or defaultNotPresentTimeout are negative.
     */
    public AbstractPsElementLocator(String description,
                                    C config,
                                    P driver,
                                    PatientWait wait,
                                    Duration defaultTimeout,
                                    Duration defaultNotPresentTimeout,
                                    Supplier<List<W>> elementSupplier,
                                    Predicate<W> elementFilter) {
        super(description, config);
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
        this.wait = validate().withMessage("Cannot use a null wait.")
                              .that(wait)
                              .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                        .that(defaultTimeout)
                                        .isGreaterThanOrEqualToZero();
        this.defaultNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                  .that(defaultNotPresentTimeout)
                                                  .isGreaterThanOrEqualToZero();
        this.elementSupplier = validate().withMessage("Cannot use a null element supplier.")
                                         .that(elementSupplier)
                                         .isNotNull();
        this.elementFilter = validate().withMessage("Cannot use a null element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
    }

    /**
     * @param description     the String description of the element.
     *                        May not be null or empty.
     * @param initialElement  the initial {@link WebElement} to have in the cache.
     *                        May not be null.
     * @param elementSupplier the supplier of {@link WebElement}s to be used for relocating
     *                        the element cache if the initial element is cleared or stale.
     *                        An exception will be thrown later if it ever returns a null.
     *                        May not be null.
     *
     * @return an {@link AbstractPsElement} instance with the given locator
     * values. Should never return null.
     */
    protected abstract E buildElement(String description,
                                      W initialElement,
                                      Supplier<W> elementSupplier);

    /*
     * We do this so that if the driver is lazily initialized it
     * won't count the driver startup time against the location of
     * an element.
     */
    private void prepareDriver() {
        driver.withWrappedDriver().accept(d -> {
            // todo : fix formatting
        });
    }

    private Supplier<W> createElementSupplier(int index,
                                              Duration timeout) {
        prepareDriver();
        return () -> {
            try {
                return wait.from(() -> {
                    // We want to break out of the stream as soon as we find the n-th value
                    // to avoid unnecessarily checking the rest of the elements against
                    // the given predicate
                    List<W> list = elementSupplier.get()
                                                  .stream()
                                                  .filter(elementFilter)
                                                  .limit(index + 1)
                                                  .collect(Collectors.toList());
                    if (list.size() >= index + 1) {
                        return list.get(index);
                    } else {
                        return null;
                    }
                }).get(timeout);
            } catch (PatientTimeoutException ignore) {
                String indexString = index == 0 ? "" : String.valueOf(index);
                NoSuchElementException exception = getConfig().getElementNotFoundExceptionBuilderFunction()
                                                              .apply(String.format(NOT_FOUND_FORMAT,
                                                                                   this,
                                                                                   indexString,
                                                                                   timeout,
                                                                                   ignore.getFailedAttemptsCount()));
                exception.initCause(ignore);
                throw exception;
            }
        };
    }

    /**
     * @return the {@link P} used to locate this element.
     */
    protected final P getDriver() {
        return driver;
    }

    /**
     * @return the {@link PatientWait} used when locating this element.
     */
    protected final PatientWait getWait() {
        return wait;
    }

    /**
     * @return the {@link Duration} used by default when locating an element that is expected
     * to be there.
     */
    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the {@link Duration} used by default when trying to check if an element is not
     * present that isn't expected to be there.
     */
    protected final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    /**
     * @return the {@link Supplier} of elements for this element locator.
     */
    protected final Supplier<List<W>> getElementSupplier() {
        return elementSupplier;
    }

    /**
     * @return the {@link Predicate} used to verify that a located element is a match.
     */
    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    /**
     * Same as calling {@link #get(int, Duration)} with an index of 0 and the default timeout.
     *
     * @return the first matching element.
     *
     * @throws IllegalArgumentException if index is negative.
     * @throws NoSuchElementException   if no n-th matching element is found and the timeout is reached.
     */
    public final E get() {
        return get(0, defaultTimeout);
    }

    /**
     * Same as calling {@link #get(int, Duration)} with an index of 0.
     *
     * @param timeout the {@link Duration} timeout.
     *                Note that a duration of 0 means try to locate an element once.
     *                May not be null or negative.
     *
     * @return the first matching element.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     * @throws NoSuchElementException   if no n-th matching element is found and the timeout is reached.
     */
    public final E get(Duration timeout) {
        return get(0, timeout);
    }

    /**
     * Same as calling {@link #get(int, Duration)} with the default timeout.
     *
     * @param index the index of the desired matching element.
     *              May not be negative.
     *
     * @return the n-th matching element.
     *
     * @throws IllegalArgumentException if index is negative.
     * @throws NoSuchElementException   if no n-th matching element is found and the timeout is reached.
     */
    public final E get(int index) {
        return get(index, defaultTimeout);
    }

    /**
     * Keep trying to locate the n-th element that matches the set requirements.
     * The index is zero based so the first element would be index 0.
     * If the timeout is reached and no n-th element is found then throw an exception.
     *
     * @param index   the index of the desired matching element.
     *                May not be negative.
     * @param timeout the {@link Duration} timeout.
     *                Note that a duration of 0 means try to locate an element once.
     *                May not be null or negative.
     *
     * @return the n-th matching element.
     *
     * @throws IllegalArgumentException if index is negative or if timeout is null or negative.
     * @throws NoSuchElementException   if no n-th matching element is found and the timeout is reached.
     */
    public final E get(int index,
                       Duration timeout) {
        validate().withMessage("Cannot locate an element with a negative index.")
                  .that(index)
                  .isAtLeast(0);
        validate().withMessage("Cannot locate an element with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        Supplier<W> elementSupplier = createElementSupplier(index, timeout);
        String indexString = index == 0 ? "" : String.valueOf(index);
        return buildElement(String.format(ELEMENT_FORMAT,
                                          this,
                                          indexString),
                            elementSupplier.get(),
                            elementSupplier);
    }

    /**
     * Same as calling {@link #getAll(Duration)} with the default timeout.
     *
     * @return a List of all matching elements as soon as any are found or an empty list if none
     * are found.
     */
    public final List<E> getAll() {
        return getAll(defaultTimeout);
    }

    /**
     * Try to locate any matching elements. As soon as at least one are found, return the
     * list of all located elements. If the timeout is reached and no matching elements
     * have been found then return an empty list.
     *
     * @param timeout the {@link Duration} timeout to keep checking for any matching elements.
     *                Note that a duration of 0 means try to locate an element once.
     *                May not be null or negative.
     *
     * @return a List of all matching elements as soon as any are found or an empty list if none
     * are found.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public final List<E> getAll(Duration timeout) {
        validate().withMessage("Cannot locate elements with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            prepareDriver();
            List<W> elements = wait.from(() -> elementSupplier.get()
                                                              .stream()
                                                              .filter(elementFilter)
                                                              .collect(Collectors.toList()))
                                   .withFilter(list -> null != list && !list.isEmpty())
                                   .get(timeout);
            List<E> builtElements = new ArrayList<>(elements.size());
            for (int i = 0; i < elements.size(); i++) {
                String indexString = i == 0 ? "" : String.valueOf(i);
                builtElements.add(buildElement(String.format(ELEMENT_FORMAT,
                                                             this,
                                                             indexString),
                                               elements.get(i),
                                               createElementSupplier(i, timeout)));
            }
            return builtElements;
        } catch (PatientTimeoutException ignore) {
            return Collections.emptyList();
        }
    }

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
    public final OptionalExecutor ifPresent(Consumer<E> consumer) {
        return ifPresent(consumer, defaultTimeout);
    }

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
    public final OptionalExecutor ifPresent(Consumer<E> consumer,
                                            Duration timeout) {
        validate().withMessage("Cannot execute with a null consumer.")
                  .that(consumer)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        OptionalExecutor optionalExecutor;
        try {
            // Supply the consumer with the found element, if any
            E element = get(0, timeout);
            // Element found, execute this consumer and not a subsequent one
            optionalExecutor = new OptionalExecutor(false);
            consumer.accept(element);
        } catch (NoSuchElementException ignore) {
            // Element not found, we will want to run the second given code block, if any
            optionalExecutor = new OptionalExecutor(true);
        }
        return optionalExecutor;
    }

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
    public final OptionalExecutor ifNotPresent(Runnable runnable) {
        return ifNotPresent(runnable, defaultNotPresentTimeout);
    }

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
    public final OptionalExecutor ifNotPresent(Runnable runnable,
                                               Duration timeout) {
        validate().withMessage("Cannot execute with a null runnable.")
                  .that(runnable)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        OptionalExecutor optionalExecutor;
        try {
            prepareDriver();
            wait.from(() -> elementSupplier.get()
                                           .stream()
                                           .noneMatch(elementFilter))
                .get(timeout);
            // An empty list was found, run this runnable and not a subsequent one
            optionalExecutor = new OptionalExecutor(false);
            runnable.run();
        } catch (PatientTimeoutException ignore) {
            // Element was still present, we will want to run the second given code block, if any
            optionalExecutor = new OptionalExecutor(true);

        }
        return optionalExecutor;
    }
}
