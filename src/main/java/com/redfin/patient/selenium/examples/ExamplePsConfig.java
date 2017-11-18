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

package com.redfin.patient.selenium.examples;

import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientRetryHandlers;
import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public final class ExamplePsConfig
        extends AbstractPsConfig<WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsConfig(PatientWait defaultIsPresentWait,
                           PatientWait defaultIsNotPresentWait,
                           Duration defaultIsPresentTimeout,
                           Duration defaultIsNotPresentTimeout,
                           Predicate<WebElement> defaultElementFilter) {
        super(defaultIsPresentWait,
              defaultIsNotPresentWait,
              defaultIsPresentTimeout,
              defaultIsNotPresentTimeout,
              defaultElementFilter);
    }

    public static ExamplePsConfigBuilder builder() {
        return new ExamplePsConfigBuilder();
    }

    public static final class ExamplePsConfigBuilder {

        private PatientWait defaultIsPresentWait;
        private PatientWait defaultIsNotPresentWait;
        private Duration defaultIsPresentTimeout;
        private Duration defaultIsNotPresentTimeout;
        private Predicate<WebElement> defaultElementFilter;

        private ExamplePsConfigBuilder() {
            Duration defaultTimeout = Duration.ofSeconds(30);
            PatientWait defaultWait = PatientWait.builder()
                                                 .withExecutionHandler(PatientExecutionHandlers.ignoring(NoSuchElementException.class))
                                                 .withRetryHandler(PatientRetryHandlers.fixedDelay(Duration.ofMillis(500)))
                                                 .withDefaultTimeout(defaultTimeout)
                                                 .build();
            defaultIsPresentWait = defaultWait;
            defaultIsNotPresentWait = defaultWait;
            defaultIsPresentTimeout = defaultTimeout;
            defaultIsNotPresentTimeout = defaultTimeout;
            defaultElementFilter = Objects::nonNull;
        }

        public ExamplePsConfigBuilder withDefaultIsPresentWait(PatientWait wait) {
            validate().withMessage("Cannot use a null wait.")
                      .that(wait)
                      .isNotNull();
            this.defaultIsPresentWait = wait;
            return this;
        }

        public ExamplePsConfigBuilder withDefaultIsNotPresentWait(PatientWait wait) {
            validate().withMessage("Cannot use a null wait.")
                      .that(wait)
                      .isNotNull();
            this.defaultIsNotPresentWait = wait;
            return this;
        }

        public ExamplePsConfigBuilder withDefaultIsPresentTimeout(Duration timeout) {
            validate().withMessage("Cannot use a null or negative timeout.")
                      .that(timeout)
                      .isGreaterThanOrEqualToZero();
            this.defaultIsPresentTimeout = timeout;
            return this;
        }

        public ExamplePsConfigBuilder withDefaultIsNotPresentTimeout(Duration timeout) {
            validate().withMessage("Cannot use a null or negative timeout.")
                      .that(timeout)
                      .isGreaterThanOrEqualToZero();
            this.defaultIsNotPresentTimeout = timeout;
            return this;
        }

        public ExamplePsConfigBuilder withDefaultElementFilter(Predicate<WebElement> elementFilter) {
            validate().withMessage("Cannot use a null element filter.")
                      .that(elementFilter)
                      .isNotNull();
            this.defaultElementFilter = elementFilter;
            return this;
        }

        public ExamplePsConfig build() {
            return new ExamplePsConfig(defaultIsPresentWait,
                                       defaultIsNotPresentWait,
                                       defaultIsPresentTimeout,
                                       defaultIsNotPresentTimeout,
                                       defaultElementFilter);
        }
    }
}
