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
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsConfig<W extends WebElement,
        THIS extends AbstractPsConfig<W, THIS, B, L, E>,
        B extends PsElementLocatorBuilder<W, THIS, B, L, E>,
        L extends PsElementLocator<W, THIS, B, L, E>,
        E extends PsElement<W, THIS, B, L, E>>
        implements PsConfig<W, THIS, B, L, E> {

    private final PatientWait defaultWait;
    private final Duration defaultTimeout;
    private final Duration defaultAssertNotPresentTimeout;
    private final Predicate<W> defaultElementFilter;
    private final Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction;

    public AbstractPsConfig(PatientWait defaultWait,
                            Duration defaultTimeout,
                            Predicate<W> defaultElementFilter,
                            Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        this(defaultWait, defaultTimeout, defaultTimeout, defaultElementFilter, elementNotFoundExceptionBuilderFunction);
    }

    public AbstractPsConfig(PatientWait defaultWait,
                            Duration defaultTimeout,
                            Duration defaultAssertNotPresentTimeout,
                            Predicate<W> defaultElementFilter,
                            Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        this.defaultWait = validate().withMessage("Cannot use a null wait.")
                                     .that(defaultWait)
                                     .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                        .that(defaultTimeout)
                                        .isGreaterThanOrEqualToZero();
        this.defaultAssertNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                        .that(defaultAssertNotPresentTimeout)
                                                        .isGreaterThanOrEqualToZero();
        this.defaultElementFilter = validate().withMessage("Cannot use a null element filter.")
                                              .that(defaultElementFilter)
                                              .isNotNull();
        this.elementNotFoundExceptionBuilderFunction = validate().withMessage("Cannot use a null element not found function.")
                                                                 .that(elementNotFoundExceptionBuilderFunction)
                                                                 .isNotNull();
    }

    @Override
    public final PatientWait getDefaultWait() {
        return defaultWait;
    }

    @Override
    public final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    @Override
    public final Duration getDefaultAssertNotPresentTimeout() {
        return defaultAssertNotPresentTimeout;
    }

    @Override
    public final Predicate<W> getDefaultElementFilter() {
        return defaultElementFilter;
    }

    @Override
    public final Function<String, NoSuchElementException> getElementNotFoundExceptionBuilderFunction() {
        return elementNotFoundExceptionBuilderFunction;
    }
}
