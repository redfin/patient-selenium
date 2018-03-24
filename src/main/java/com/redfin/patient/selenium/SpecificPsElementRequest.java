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
public final class SpecificPsElementRequest<D extends WebDriver,
                                            W extends WebElement,
                                            C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                            P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                            B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                            L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                            E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private final BiFunction<Integer, Duration, E> specificElementRequestFunction;
    private final int index;
    private final Duration defaultTimeout;

    /**
     * Creates a new SpecificPsElementRequest instance with the given values.
     *
     * @param specificElementRequestFunction the function that takes in an index integer and a duration timeout and returns
     *                                       the requested element. May not be null.
     *                                       It should never return a null object.
     * @param index                          the int (0-based) index for the specific element to be requested.
     *                                       May not be negative.
     * @param defaultTimeout                 the Duration to be used as a timeout for the {@link #get()} method.
     *                                       May not be null or negative.
     *
     * @throws IllegalArgumentException if specificElementRequestFunction or defaultTimeout are null or if index or defaultTimeout
     * are negative.
     */
    SpecificPsElementRequest(BiFunction<Integer, Duration, E> specificElementRequestFunction,
                             int index,
                             Duration defaultTimeout) {
        this.specificElementRequestFunction = validate().withMessage("Cannot request a specific element with a null element request function.")
                                                        .that(specificElementRequestFunction)
                                                        .isNotNull();
        this.index = validate().withMessage("Cannot request a specific element with a negative index.")
                               .that(index)
                               .isAtLeast(0);
        this.defaultTimeout = validate().withMessage("Cannot request a specific element with a null or negative default timeout")
                                        .that(defaultTimeout)
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

    /**
     * Keep trying to locate the n-th element that matches the set requirements.
     * The index is zero based so the first element would be index 0.
     * If the timeout is reached and no n-th element is found then throw an exception.
     *
     * @return the requested element instance.
     *
     * @throws NoSuchElementException if no matching element is found within the default timeout.
     */
    public E get() {
        return get(defaultTimeout);
    }

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
    public E get(Duration timeout) {
        validate().withMessage("Cannot locate a specific element with a null or negative timeout.")
                  .that(timeout)
                  .isAtLeast(Duration.ZERO);
        return expect().withMessage("Received a null element object from the specific element request function.")
                       .that(specificElementRequestFunction.apply(index, timeout))
                       .isNotNull();
    }
}
