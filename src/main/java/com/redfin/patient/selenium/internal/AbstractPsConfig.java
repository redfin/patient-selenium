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
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsConfig<W extends WebElement,
        THIS extends AbstractPsConfig<W, THIS, B, L, E>,
        B extends PsElementLocatorBuilder<W, THIS, B, L, E>,
        L extends PsElementLocator<W, THIS, B, L, E>,
        E extends PsElement<W, THIS, B, L, E>>
        implements PsConfig<W, THIS, B, L, E> {

    private final PatientWait defaultIsPresentWait;
    private final PatientWait defaultIsNotPresentWait;
    private final Duration defaultIsPresentTimeout;
    private final Duration defaultIsNotPresentTimeout;
    private final Predicate<W> defaultElementFilter;

    public AbstractPsConfig(PatientWait defaultIsPresentWait,
                            PatientWait defaultIsNotPresentWait,
                            Duration defaultIsPresentTimeout,
                            Duration defaultIsNotPresentTimeout,
                            Predicate<W> defaultElementFilter) {
        this.defaultIsPresentWait = validate().withMessage("Cannot use a null wait.")
                                              .that(defaultIsPresentWait)
                                              .isNotNull();
        this.defaultIsNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                                 .that(defaultIsNotPresentWait)
                                                 .isNotNull();
        this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                 .that(defaultIsPresentTimeout)
                                                 .isGreaterThanOrEqualToZero();
        this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                    .that(defaultIsNotPresentTimeout)
                                                    .isGreaterThanOrEqualToZero();
        this.defaultElementFilter = validate().withMessage("Cannot use a null element filter.")
                                              .that(defaultElementFilter)
                                              .isNotNull();
    }

    @Override
    public final PatientWait getDefaultIsPresentWait() {
        return defaultIsPresentWait;
    }

    @Override
    public final PatientWait getDefaultIsNotPresentWait() {
        return defaultIsNotPresentWait;
    }

    @Override
    public final Duration getDefaultIsPresentTimeout() {
        return defaultIsPresentTimeout;
    }

    @Override
    public final Duration getDefaultIsNotPresentTimeout() {
        return defaultIsNotPresentTimeout;
    }

    @Override
    public final Predicate<W> getDefaultElementFilter() {
        return defaultElementFilter;
    }
}
