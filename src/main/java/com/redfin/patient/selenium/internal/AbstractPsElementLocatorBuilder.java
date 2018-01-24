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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

/**
 * Base type for an element locator builder.
 * This is a mutable type that allows you to set up the values to be
 * used for the element locator to be built via the {@link #build(String, Supplier)}
 * method.
 *
 * @param <D>    the type of wrapped WebDriver.
 * @param <W>    the type of wrapped WebElement.
 * @param <C>    the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P>    the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <THIS> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L>    the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E>    the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsElementLocatorBuilder<D extends WebDriver,
                                                      W extends WebElement,
                                                      C extends AbstractPsConfig<D, W, C, P, THIS, L, E>,
                                                      P extends AbstractPsDriver<D, W, C, P, THIS, L, E>,
                                                   THIS extends AbstractPsElementLocatorBuilder<D, W, C, P, THIS, L, E>,
                                                      L extends AbstractPsElementLocator<D, W, C, P, THIS, L, E>,
                                                      E extends AbstractPsElement<D, W, C, P, THIS, L, E>>
              extends AbstractPsBase<D, W, C, P, THIS, L, E> {

    private static final String BY_FORMAT = "%s.by(%s)";

    private final P driver;
    private final Function<By, List<W>> baseSeleniumLocatorFunction;

    private PatientWait wait;
    private Duration defaultTimeout;
    private Duration defaultNotPresentTimeout;
    private Predicate<W> elementFilter;

    /**
     * Create a new {@link AbstractPsElementLocatorBuilder} instance.
     *
     * @param description                 the String description of the context that will be
     *                                    searched within.
     *                                    May not be null or empty.
     * @param config                      the {@link AbstractPsConfig} to be used.
     *                                    May not be null.
     * @param driver                      the {@link AbstractPsDriver} instance used as the outer
     *                                    search context.
     *                                    May not be null.
     * @param baseSeleniumLocatorFunction the function used to take a {@link By} and return
     *                                    a list of {@link WebElement}s.
     *                                    May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if the description is empty.
     */
    public AbstractPsElementLocatorBuilder(String description,
                                           C config,
                                           P driver,
                                           Function<By, List<W>> baseSeleniumLocatorFunction) {
        super(description, config);
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
        this.baseSeleniumLocatorFunction = validate().withMessage("Cannot use a null base selenium locator function.")
                                                     .that(baseSeleniumLocatorFunction)
                                                     .isNotNull();
        this.wait = config.getDefaultWait();
        this.defaultTimeout = config.getDefaultTimeout();
        this.defaultNotPresentTimeout = config.getDefaultNotPresentTimeout();
        this.elementFilter = config.getDefaultElementFilter();
    }

    /**
     * @return a self reference.
     */
    protected abstract THIS getThis();

    /**
     * @param description     the String description of the built element locator.
     *                        May not be null or empty.
     * @param elementSupplier the supplier of elements for the element locator.
     *                        May not be null.
     *
     * @return an element locator built from this builder.
     *
     * @throws IllegalArgumentException if description or elementSupplier are null
     *                                  or if description is empty.
     */
    protected abstract L build(String description,
                               Supplier<List<W>> elementSupplier);

    /**
     * @return the driver used as the outer search context for this.
     */
    protected final P getDriver() {
        return driver;
    }

    /**
     * @return the function used to take a {@link By} and return a list of {@link WebElement}s.
     */
    protected final Function<By, List<W>> getBaseSeleniumLocatorFunction() {
        return baseSeleniumLocatorFunction;
    }

    /**
     * @return the {@link PatientWait} for this builder.
     */
    protected final PatientWait getWait() {
        return wait;
    }

    /**
     * @return the {@link Duration} default timeout for this builder.
     */
    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the {@link Duration} default not present timeout for this builder.
     */
    protected final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    /**
     * @return the {@link Predicate} filter for selenium elements for this builder.
     */
    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    /**
     * @param wait the {@link PatientWait} used for when locating elements.
     *             May not be null.
     *
     * @return a self reference.
     *
     * @throws IllegalArgumentException if wait is null.
     */
    public final THIS withWait(PatientWait wait) {
        validate().withMessage("Cannot use a null wait.")
                  .that(wait)
                  .isNotNull();
        this.wait = wait;
        return getThis();
    }

    /**
     * @param timeout the default {@link Duration} to be used when trying
     *                to locate an element.
     *                May not be null or negative.
     *
     * @return a self reference.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public final THIS withTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultTimeout = timeout;
        return getThis();
    }

    /**
     * @param timeout the default {@link Duration} timeout to be used
     *                when checking that an element is not present.
     *                May not be null or negative.
     *
     * @return a self reference.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public final THIS withNotPresentTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultNotPresentTimeout = timeout;
        return getThis();
    }

    /**
     * @param elementFilter the {@link Predicate} to be used to check
     *                      returned Selenium {@link WebElement}s when
     *                      locating requested elements. Any element
     *                      that doesn't pass the predicate won't be
     *                      considered a match.
     *                      May not be null.
     *
     * @return a self reference.
     *
     * @throws IllegalArgumentException if elementFilter is null.
     */
    public final THIS withFilter(Predicate<W> elementFilter) {
        validate().withMessage("Cannot use a null element filter.")
                  .that(elementFilter)
                  .isNotNull();
        this.elementFilter = elementFilter;
        return getThis();
    }

    /**
     * @param locator the {@link By} locator for finding elements.
     *                May not be null.
     *
     * @return the element locator built from the current settings of this builder.
     *
     * @throws IllegalArgumentException if locator is null.
     */
    public final L by(By locator) {
        validate().withMessage("Cannot find elements with a null locator.")
                  .that(locator)
                  .isNotNull();
        Supplier<List<W>> elementSupplier = () -> baseSeleniumLocatorFunction.apply(locator);
        return build(String.format(BY_FORMAT,
                                   getDescription(),
                                   locator),
                     elementSupplier);
    }
}
