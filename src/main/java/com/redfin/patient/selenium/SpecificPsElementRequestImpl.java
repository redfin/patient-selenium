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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Final class used to be in in-between state for locating an element. It contains the index
 * requested and is used to hold state until a corresponding method is called to actually
 * locate the element.
 *
 * @param <D> the type of wrapped WebDriver.
 * @param <W> the type of wrapped WebElement.
 * @param <C> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E> the type of {@link AbstractPsElement} used by the implementing type.
 */
public final class SpecificPsElementRequestImpl<D extends WebDriver,
                                                W extends WebElement,
                                                C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                                P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                                B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                                L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                                E extends AbstractPsElement<D, W, C, P, B, L, E>>
        implements SpecificElementRequest<D, W, C, P, B, L, E> {

    private final BiFunction<Integer, Duration, E> specificElementRequestFunction;
    private final BiFunction<Integer, Duration, Boolean> noMatchingElementFunction;
    private final int index;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;

    /**
     * Creates a new SpecificPsElementRequestImpl instance with the given values.
     *
     * @param specificElementRequestFunction the function that takes in an index integer and a duration timeout and returns
     *                                       the requested element.
     *                                       May not be null and It should never return a null object.
     * @param noMatchingElementFunction      the function that takes in an index integer and a duration timeout and returns
     *                                       true if there is NOT a matching element or false if there is and the timeout is reached.
     *                                       May not be null.
     * @param index                          the int (0-based) index for the specific element to be requested.
     *                                       May not be negative.
     * @param defaultTimeout                 the Duration to be used as a timeout for the methods where the element is expected
     *                                       to be present.
     *                                       May not be null or negative.
     * @param defaultNotPresentTimeout       the Duration to be used as a timeout for the methods where the element is not
     *                                       expected to be present.
     *                                       May not be null or negative.
     *
     * @throws IllegalArgumentException if specificElementRequestFunction, defaultTimeout, or defaultNotPresentTimeout are null
     *                                  or if index, defaultTimeout, or defaultNotPresentTimeout are negative.
     */
    SpecificPsElementRequestImpl(BiFunction<Integer, Duration, E> specificElementRequestFunction,
                                 BiFunction<Integer, Duration, Boolean> noMatchingElementFunction,
                                 int index,
                                 Duration defaultTimeout,
                                 Duration defaultNotPresentTimeout) {
        this.specificElementRequestFunction = validate().withMessage("Cannot request a specific element with a null element request function.")
                                                        .that(specificElementRequestFunction)
                                                        .isNotNull();
        this.noMatchingElementFunction = validate().withMessage("Cannot request a specific element with a null no matching element function.")
                                                   .that(noMatchingElementFunction)
                                                   .isNotNull();
        this.index = validate().withMessage("Cannot request a specific element with a negative index.")
                               .that(index)
                               .isAtLeast(0);
        this.defaultTimeout = validate().withMessage("Cannot request a specific element with a null or negative default timeout.")
                                        .that(defaultTimeout)
                                        .isAtLeast(Duration.ZERO);
        this.defaultNotPresentTimeout = validate().withMessage("Cannot request a specific element with a null or negative default not present timeout.")
                                                  .that(defaultNotPresentTimeout)
                                                  .isAtLeast(Duration.ZERO);
    }

    /**
     * @return the given index.
     */
    int getIndex() {
        return index;
    }

    /**
     * @return the given default timeout duration.
     */
    Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    @Override
    public E get() {
        return get(defaultTimeout);
    }

    @Override
    public E get(Duration timeout) {
        validate().withMessage("Cannot locate a specific element with a null or negative timeout.")
                  .that(timeout)
                  .isAtLeast(Duration.ZERO);
        return expect().withMessage("Received a null element object from the specific element request function.")
                       .that(specificElementRequestFunction.apply(index, timeout))
                       .isNotNull();
    }


    @Override
    public OptionalExecutor ifPresent(Consumer<E> consumer) {
        return ifPresent(consumer, defaultTimeout);
    }

    @Override
    public OptionalExecutor ifPresent(Consumer<E> consumer,
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
            E element = get(timeout);
            // Element found, execute this consumer and not a subsequent one
            optionalExecutor = new OptionalExecutor(false);
            consumer.accept(element);
        } catch (NoSuchElementException ignore) {
            // Element not found, we will want to run the second given code block, if any
            optionalExecutor = new OptionalExecutor(true);
        }
        return optionalExecutor;
    }

    @Override
    public OptionalExecutor ifNotPresent(Runnable runnable) {
        return ifNotPresent(runnable, defaultNotPresentTimeout);
    }

    @Override
    public OptionalExecutor ifNotPresent(Runnable runnable,
                                         Duration timeout) {
        validate().withMessage("Cannot execute with a null runnable.")
                  .that(runnable)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        OptionalExecutor optionalExecutor;
        if (noMatchingElementFunction.apply(index, timeout)) {
            // No matching element was found, run this runnable and not a subsequent one
            optionalExecutor = new OptionalExecutor(false);
            runnable.run();
        } else {
            // Element was still present, we will want to run the second given code block, if any
            optionalExecutor = new OptionalExecutor(true);
        }
        return optionalExecutor;
    }
}
