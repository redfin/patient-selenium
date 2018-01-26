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

package example;

import com.redfin.patience.DelaySuppliers;
import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.AbstractPsConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public class ExampleConfig
     extends AbstractPsConfig<WebDriver,
                              WebElement,
                              ExampleConfig,
                              ExampleDriver,
                              ExampleElementLocatorBuilder,
                              ExampleElementLocator,
                              ExampleElement> {

    private final Consumer<String> logConsumer;

    public ExampleConfig(Consumer<String> logConsumer,
                         PatientWait defaultWait,
                         Duration defaultTimeout,
                         Duration defaultNotPresentTimeout,
                         Predicate<WebElement> defaultElementFilter,
                         Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultNotPresentTimeout,
              defaultElementFilter,
              elementNotFoundExceptionBuilderFunction);
        this.logConsumer = validate().withMessage("Cannot use a null log consumer.")
                                     .that(logConsumer)
                                     .isNotNull();
    }

    public final Consumer<String> getLogConsumer() {
        return logConsumer;
    }

    public static PatientWebConfigBuilder builder() {
        return new PatientWebConfigBuilder();
    }

    public static class PatientWebConfigBuilder {

        private Consumer<String> logConsumer;
        private PatientWait defaultWait;
        private Duration defaultTimeout;
        private Duration defaultNotPresentTimeout;
        private Predicate<WebElement> defaultElementFilter;
        private Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction;

        private PatientWebConfigBuilder() {
            logConsumer = System.out::println;
            Duration timeout = Duration.ofSeconds(30);
            this.defaultWait = PatientWait.builder()
                                          .withInitialDelay(Duration.ZERO)
                                          .withExecutionHandler(PatientExecutionHandlers.ignoring(NoSuchElementException.class,
                                                                                                  StaleElementReferenceException.class))
                                          .withDelaySupplier(DelaySuppliers.fixed(Duration.ofMillis(500)))
                                          .withDefaultTimeout(timeout)
                                          .build();
            this.defaultTimeout = timeout;
            this.defaultNotPresentTimeout = Duration.ofSeconds(10);
            this.defaultElementFilter = Objects::nonNull;
            this.elementNotFoundExceptionBuilderFunction = NoSuchElementException::new;
        }

        public PatientWebConfigBuilder withLogConsumer(Consumer<String> logConsumer) {
            this.logConsumer = validate().withMessage("Cannot use a null consumer.")
                                         .that(logConsumer)
                                         .isNotNull();
            return this;
        }

        public PatientWebConfigBuilder withDefaultWait(PatientWait defaultWait) {
            this.defaultWait = validate().withMessage("Cannot use a null wait.")
                                         .that(defaultWait)
                                         .isNotNull();
            return this;
        }

        public PatientWebConfigBuilder withDefaultTimeout(Duration defaultTimeout) {
            this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                            .that(defaultTimeout)
                                            .isGreaterThanOrEqualToZero();
            return this;
        }

        public PatientWebConfigBuilder withDefaultNotPresentTimeout(Duration defaultNotPresentTimeout) {
            this.defaultNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                      .that(defaultNotPresentTimeout)
                                                      .isGreaterThanOrEqualToZero();
            return this;
        }

        public PatientWebConfigBuilder withDefaultElementFilter(Predicate<WebElement> defaultElementFilter) {
            this.defaultElementFilter = validate().withMessage("Cannot use a null element filter.")
                                                  .that(defaultElementFilter)
                                                  .isNotNull();
            return this;
        }

        public PatientWebConfigBuilder withElementNotFoundExceptionBuilderFunction(Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
            this.elementNotFoundExceptionBuilderFunction = validate().withMessage("Cannot use a null exception builder function.")
                                                                     .that(elementNotFoundExceptionBuilderFunction)
                                                                     .isNotNull();
            return this;
        }

        public ExampleConfig build() {
            return new ExampleConfig(logConsumer,
                                     defaultWait,
                                     defaultTimeout,
                                     defaultNotPresentTimeout,
                                     defaultElementFilter,
                                     elementNotFoundExceptionBuilderFunction);
        }
    }
}
