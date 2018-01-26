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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * Base type for a configuration object.
 * The concrete subclass is handed around between the different objects created
 * by Patient-Selenium.
 *
 * @param <D>    the type of wrapped WebDriver.
 * @param <W>    the type of wrapped WebElement.
 * @param <THIS> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P>    the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B>    the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L>    the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E>    the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsConfig<D extends WebDriver,
                                       W extends WebElement,
                                    THIS extends AbstractPsConfig<D, W, THIS, P, B, L, E>,
                                       P extends AbstractPsDriver<D, W, THIS, P, B, L, E>,
                                       B extends AbstractPsElementLocatorBuilder<D, W, THIS, P, B, L, E>,
                                       L extends AbstractPsElementLocator<D, W, THIS, P, B, L, E>,
                                       E extends AbstractPsElement<D, W, THIS, P, B, L, E>> {

    private final PatientWait defaultWait;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;
    private final Predicate<W> defaultElementFilter;
    private final Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction;

    /**
     * Create a new {@link AbstractPsConfig} instance.
     *
     * @param defaultWait                             the {@link PatientWait} to be used by default.
     *                                                May not be null.
     * @param defaultTimeout                          the {@link Duration} to be used as the default timeout for elements
     *                                                that are expected to be present.
     *                                                May not be null or negative.
     * @param defaultNotPresentTimeout                the {@link Duration} to be used as the default timeout for
     *                                                elements that are expected to not be present.
     *                                                May not be null or negative.
     * @param defaultElementFilter                    the default {@link Predicate} to use when checking elements
     *                                                to find out if they are a match for requirements other than
     *                                                simple {@link org.openqa.selenium.By} that located them.
     *                                                May not be null.
     * @param elementNotFoundExceptionBuilderFunction a {@link Function} that takes in a string and returns
     *                                                a new {@link NoSuchElementException} in the case of an element
     *                                                being expected that wasn't present.
     *                                                An exception will be thrown if this ever returns null.
     *                                                May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if the timeouts are negative.
     */
    public AbstractPsConfig(PatientWait defaultWait,
                            Duration defaultTimeout,
                            Duration defaultNotPresentTimeout,
                            Predicate<W> defaultElementFilter,
                            Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        this.defaultWait = validate().withMessage("Cannot use a null wait.")
                                     .that(defaultWait)
                                     .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                        .that(defaultTimeout)
                                        .isGreaterThanOrEqualToZero();
        this.defaultNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                  .that(defaultNotPresentTimeout)
                                                  .isGreaterThanOrEqualToZero();
        this.defaultElementFilter = validate().withMessage("Cannot use a null element filter.")
                                              .that(defaultElementFilter)
                                              .isNotNull();
        this.elementNotFoundExceptionBuilderFunction = validate().withMessage("Cannot use a null element not found function.")
                                                                 .that(elementNotFoundExceptionBuilderFunction)
                                                                 .isNotNull();
    }

    /**
     * @return the default {@link PatientWait} used to wait for elements.
     */
    public final PatientWait getDefaultWait() {
        return defaultWait;
    }

    /**
     * @return the default {@link Duration} timeout for elements that are
     * expected to be present.
     */
    public final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the default {@link Duration} timeout for elements that are
     * expected to not be present.
     */
    public final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    /**
     * @return the default {@link Predicate} for checking if a located element
     * should be considered a match.
     */
    public final Predicate<W> getDefaultElementFilter() {
        return defaultElementFilter;
    }

    /**
     * @return the function that takes in a string and returns an exception for
     * when elements cannot be located and should throw an exception.
     */
    public final Function<String, NoSuchElementException> getElementNotFoundExceptionBuilderFunction() {
        return elementNotFoundExceptionBuilderFunction;
    }
}
