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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

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

    public final PatientWait getDefaultWait() {
        return defaultWait;
    }

    public final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    public final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    public final Predicate<W> getDefaultElementFilter() {
        return defaultElementFilter;
    }

    public final Function<String, NoSuchElementException> getElementNotFoundExceptionBuilderFunction() {
        return elementNotFoundExceptionBuilderFunction;
    }
}
