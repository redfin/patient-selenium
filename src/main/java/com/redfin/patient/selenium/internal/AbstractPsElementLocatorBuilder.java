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
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElementLocatorBuilder<W extends WebElement,
        C extends PsConfig<W, C, THIS, L, E>,
        THIS extends AbstractPsElementLocatorBuilder<W, C, THIS, L, E>,
        L extends PsElementLocator<W, C, THIS, L, E>,
        E extends PsElement<W, C, THIS, L, E>>
        extends AbstractPsBase<W, C, THIS, L, E>
        implements PsElementLocatorBuilder<W, C, THIS, L, E> {

    private static final String BY_FORMAT = "%s.by(%s)";

    private final Function<By, List<W>> baseSeleniumLocatorFunction;

    private PatientWait isPresentWait;
    private PatientWait isNotPresentWait;
    private Duration isPresentTimeout;
    private Duration isNotPresentTimeout;
    private Predicate<W> elementFilter;

    public AbstractPsElementLocatorBuilder(String description,
                                           C config,
                                           Function<By, List<W>> baseSeleniumLocatorFunction) {
        super(description, config);
        this.baseSeleniumLocatorFunction = validate().withMessage("Cannot use a null base selenium locator function.")
                                                     .that(baseSeleniumLocatorFunction)
                                                     .isNotNull();
        this.isPresentWait = config.getDefaultIsPresentWait();
        this.isNotPresentWait = config.getDefaultIsNotPresentWait();
        this.isPresentTimeout = config.getDefaultIsPresentTimeout();
        this.isNotPresentTimeout = config.getDefaultIsNotPresentTimeout();
        this.elementFilter = config.getDefaultElementFilter();
    }

    protected final Function<By, List<W>> getBaseSeleniumLocatorFunction() {
        return baseSeleniumLocatorFunction;
    }

    protected final PatientWait getIsPresentWait() {
        return isPresentWait;
    }

    protected final PatientWait getIsNotPresentWait() {
        return isNotPresentWait;
    }

    protected final Duration getIsPresentTimeout() {
        return isPresentTimeout;
    }

    protected final Duration getIsNotPresentTimeout() {
        return isNotPresentTimeout;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    protected abstract THIS getThis();

    protected abstract L build(String description,
                               Supplier<List<W>> elementSupplier,
                               Predicate<W> elementFilter);

    @Override
    public THIS withIsPresentWait(PatientWait wait) {
        validate().withMessage("Cannot use a null wait.")
                  .that(wait)
                  .isNotNull();
        this.isPresentWait = wait;
        return getThis();
    }

    @Override
    public THIS withIsNotPresentWait(PatientWait wait) {
        validate().withMessage("Cannot use a null wait.")
                  .that(wait)
                  .isNotNull();
        this.isNotPresentWait = wait;
        return getThis();
    }

    @Override
    public THIS withIsPresentTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.isPresentTimeout = timeout;
        return getThis();
    }

    @Override
    public THIS withIsNotPresentTimeout(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        this.isNotPresentTimeout = timeout;
        return getThis();
    }

    @Override
    public THIS withFilter(Predicate<W> elementFilter) {
        validate().withMessage("Cannot use a null element filter.")
                  .that(elementFilter)
                  .isNotNull();
        this.elementFilter = elementFilter;
        return getThis();
    }

    @Override
    public L by(By locator) {
        validate().withMessage("Cannot find elements with a null locator.")
                  .that(locator)
                  .isNotNull();
        Supplier<List<W>> elementSupplier = () -> baseSeleniumLocatorFunction.apply(locator);
        return build(String.format(BY_FORMAT,
                                   getDescription(),
                                   locator),
                     elementSupplier,
                     getElementFilter());
    }
}
