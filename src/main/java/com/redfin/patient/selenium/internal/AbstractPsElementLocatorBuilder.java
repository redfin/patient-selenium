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
        extends AbstractPsBase<D, W, C, P, THIS, L, E> {

    private static final String BY_FORMAT = "%s.by(%s)";

    private final P driver;
    private final Function<By, List<W>> baseSeleniumLocatorFunction;

    private PatientWait wait;
    private Duration defaultTimeout;
    private Duration defaultNotPresentTimeout;
    private Predicate<W> elementFilter;

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

    protected abstract THIS getThis();

    protected abstract L build(String description,
                               Supplier<List<W>> elementSupplier);

    protected final P getDriver() {
        return driver;
    }

    protected final Function<By, List<W>> getBaseSeleniumLocatorFunction() {
        return baseSeleniumLocatorFunction;
    }

    protected final PatientWait getWait() {
        return wait;
    }

    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    protected final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    public final THIS withWait(PatientWait wait) {
        validate().withMessage("Cannot use a null wait.")
                  .that(wait)
                  .isNotNull();
        this.wait = wait;
        return getThis();
    }

    public final THIS withTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultTimeout = timeout;
        return getThis();
    }

    public final THIS withNotPresentTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.defaultNotPresentTimeout = timeout;
        return getThis();
    }

    public final THIS withFilter(Predicate<W> elementFilter) {
        validate().withMessage("Cannot use a null element filter.")
                  .that(elementFilter)
                  .isNotNull();
        this.elementFilter = elementFilter;
        return getThis();
    }

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
