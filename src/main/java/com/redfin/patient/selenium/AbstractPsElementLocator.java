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

import com.redfin.patience.PatientWait;
import com.redfin.patience.exceptions.PatientTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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
              extends AbstractPsBase<D, W, C, P, B, THIS, E>
           implements SpecificElementRequest<D, W, C, P, B, THIS, E> {

    private static final String ELEMENT_FORMAT = "%s.get(%s)";
    private static final String NOT_FOUND_FORMAT = "No element found matching %s.get(%s) within %s after %d attempts";

    private final P driver;
    private final PatientWait wait;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;
    private final Supplier<List<W>> elementSupplier;
    private final Predicate<W> elementFilter;
    private final Function<By, List<W>> baseSeleniumLocatorFunction;

    private final Map<Integer, SpecificPsElementRequestImpl<D, W, C, P, B, THIS, E>> specificElementRequestCache = new HashMap<>();

    /**
     * Create a new AbstractPsElementLocator with the given values.
     *
     * @param description                 the string description of the element locator.
     *                                    May not be empty or null.
     * @param config                      the {@link AbstractPsConfig} for this element locator.
     *                                    May not be null.
     * @param driver                      the {@link AbstractPsDriver} for this element locator.
     *                                    May not be null.
     * @param wait                        the {@link PatientWait} for this element locator.
     *                                    may not be null.
     * @param defaultTimeout              the default {@link Duration} timeout when locating an
     *                                    element you expect to be present.
     *                                    May not be null or negative.
     * @param defaultNotPresentTimeout    the default {@link Duration} timeout when trying
     *                                    to check that an element is not present that you
     *                                    don't expect to be there.
     *                                    May not be null or negative.
     * @param elementSupplier             the {@link Supplier} of a list of {@link WebElement}s.
     *                                    An exception will be thrown later if it ever returns a null list.
     *                                    May not be null.
     * @param elementFilter               the {@link Predicate} to use to check elements returned from the
     *                                    supplier. Elements that don't pass the predicate won't be considered
     *                                    a valid match.
     *                                    May not be null.
     * @param baseSeleniumLocatorFunction the function that takes in a {@link By} and returns a list of
     *                                    specific web elements.
     *                                    May not be null
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
                                    Predicate<W> elementFilter,
                                    Function<By, List<W>> baseSeleniumLocatorFunction) {
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
        this.baseSeleniumLocatorFunction = validate().withMessage("Cannot use a null selenium locator function")
                                                     .that(baseSeleniumLocatorFunction)
                                                     .isNotNull();
    }

    /**
     * @return a new element locator builder {@link B} that is set to the same defaults as
     * this locator.
     */
    public abstract B builder();

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
     * won't count the driver startup time against the timeout for
     * locating the element.
     */

    private void prepareDriver() {
        driver.withWrappedDriver()
              .accept(d -> { });
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
     * @return the {@link Function} used to locate a list of elements with a selenium by.
     */
    protected final Function<By, List<W>> getBaseSeleniumLocatorFunction() {
        return baseSeleniumLocatorFunction;
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
     * @param index the int index for the desired element to be returned (0 based indexing).
     *
     * @return a {@link SpecificPsElementRequestImpl} instance with the given index request.
     *
     * @throws IllegalArgumentException if index is negative.
     */
    public final SpecificPsElementRequestImpl<D, W, C, P, B, THIS, E> atIndex(int index) {
        return specificElementRequestCache.computeIfAbsent(index, it -> {
            BiFunction<Integer, Duration, E> specificElementRequestFunction = (i, timeout) -> {
                prepareDriver();
                Supplier<W> elementSupplier = createElementSupplier(i, timeout);
                String indexString = i == 0 ? "" : String.valueOf(i);
                return buildElement(String.format(ELEMENT_FORMAT,
                                                  this,
                                                  indexString),
                                    elementSupplier.get(),
                                    elementSupplier);
            };
            BiFunction<Integer, Duration, Boolean> noMatchingElementFunction = (i, timeout) -> {
                try {
                    wait.from(() -> elementSupplier.get()
                                                   .stream()
                                                   .noneMatch(elementFilter))
                        .get(timeout);
                    // Empty list found, return true
                    return true;
                } catch (PatientTimeoutException ignore) {
                    // Timeout reached but matching elements still found, return false
                    return false;
                }
            };
            return new SpecificPsElementRequestImpl<>(specificElementRequestFunction,
                                                      noMatchingElementFunction,
                                                      it,
                                                      defaultTimeout,
                                                      defaultNotPresentTimeout);
        });
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#get()} on the result.
     *
     * @see SpecificElementRequest#get()
     */
    @Override
    public final E get() {
        return atIndex(0).get(defaultTimeout);
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#get(Duration)} on the result.
     *
     * @see SpecificElementRequest#get(Duration)
     */
    @Override
    public final E get(Duration timeout) {
        return atIndex(0).get(timeout);
    }

    @Override
    public boolean isPresent() {
        return isPresent(defaultTimeout);
    }

    @Override
    public boolean isPresent(Duration timeout) {
        AtomicBoolean present = new AtomicBoolean(false);
        ifPresent(e -> present.set(true));
        return present.get();
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#ifPresent(Consumer)} on the result.
     *
     * @see SpecificElementRequest#ifPresent(Consumer)
     */
    @Override
    public final OptionalExecutor ifPresent(Consumer<E> consumer) {
        return ifPresent(consumer, defaultTimeout);
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#ifPresent(Consumer, Duration)} on the result.
     *
     * @see SpecificElementRequest#ifPresent(Consumer, Duration)
     */
    @Override
    public final OptionalExecutor ifPresent(Consumer<E> consumer,
                                            Duration timeout) {
        return atIndex(0).ifPresent(consumer, timeout);
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#ifNotPresent(Runnable)} on the result.
     *
     * @see SpecificElementRequest#ifNotPresent(Runnable)
     */
    @Override
    public final OptionalExecutor ifNotPresent(Runnable runnable) {
        return atIndex(0).ifNotPresent(runnable);
    }

    /**
     * Syntactic sugar for calling {@link #atIndex(int)} with a value of 0 and then calling
     * {@link SpecificElementRequest#ifNotPresent(Runnable, Duration)} on the result.
     *
     * @see SpecificElementRequest#ifNotPresent(Runnable, Duration)
     */
    @Override
    public final OptionalExecutor ifNotPresent(Runnable runnable,
                                               Duration timeout) {
        return atIndex(0).ifNotPresent(runnable, timeout);
    }
}
