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

public abstract class AbstractPsElementLocatorBuilder<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, THIS, L, E>,
        P extends AbstractPsDriver<D, W, C, P, THIS, L, E>,
        THIS extends AbstractPsElementLocatorBuilder<D, W, C, P, THIS, L, E>,
        L extends AbstractPsElementLocator<D, W, C, P, THIS, L, E>,
        E extends AbstractPsElement<D, W, C, P, THIS, L, E>>
        extends AbstractPsBase<W, C, THIS, L, E>
        implements PsElementLocatorBuilder<W, C, THIS, L, E> {

    private static final String BY_FORMAT = "%s.by(%s)";

    private final Function<By, List<W>> baseSeleniumLocatorFunction;

    private PatientWait defaultWait;
    private Duration defaultTimeout;
    private Duration defaultAssertNotPresentTimeout;
    private Predicate<W> elementFilter;
    private P driver;

    public AbstractPsElementLocatorBuilder(String description,
                                           C config,
                                           Function<By, List<W>> baseSeleniumLocatorFunction,
                                           P driver) {
        super(description, config);
        this.baseSeleniumLocatorFunction = validate().withMessage("Cannot use a null base selenium locator function.")
                                                     .that(baseSeleniumLocatorFunction)
                                                     .isNotNull();
        this.defaultWait = config.getDefaultWait();
        this.defaultTimeout = config.getDefaultTimeout();
        this.defaultAssertNotPresentTimeout = config.getDefaultAssertNotPresentTimeout();
        this.elementFilter = config.getDefaultElementFilter();
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
    }

    protected final Function<By, List<W>> getBaseSeleniumLocatorFunction() {
        return baseSeleniumLocatorFunction;
    }

    protected final PatientWait getDefaultWait() {
        return defaultWait;
    }

    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    protected final Duration getDefaultAssertNotPresentTimeout() {
        return defaultAssertNotPresentTimeout;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    protected final P getDriver() {
        return driver;
    }

    protected abstract THIS getThis();

    protected abstract L build(String description,
                               Supplier<List<W>> elementSupplier);

    @Override
    public final THIS withWait(PatientWait wait) {
        validate().withMessage("Cannot use a null wait.")
                  .that(wait)
                  .isNotNull();
        this.defaultWait = wait;
        return getThis();
    }

    @Override
    public final THIS withTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultTimeout = timeout;
        return getThis();
    }

    @Override
    public final THIS withAssertNotPresentTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultAssertNotPresentTimeout = timeout;
        return getThis();
    }

    @Override
    public final THIS withFilter(Predicate<W> elementFilter) {
        validate().withMessage("Cannot use a null element filter.")
                  .that(elementFilter)
                  .isNotNull();
        this.elementFilter = elementFilter;
        return getThis();
    }

    @Override
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
