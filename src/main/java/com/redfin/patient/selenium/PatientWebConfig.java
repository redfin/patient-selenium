package com.redfin.patient.selenium;

import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientRetryHandlers;
import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsConfig;
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

public class PatientWebConfig
        extends AbstractPsConfig<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    private final Consumer<String> logConsumer;

    public PatientWebConfig(Consumer<String> logConsumer,
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
                                          .withExecutionHandler(PatientExecutionHandlers.ignoring(NoSuchElementException.class,
                                                                                                  StaleElementReferenceException.class))
                                          .withRetryHandler(PatientRetryHandlers.fixedDelay(Duration.ofMillis(500)))
                                          .withDefaultTimeout(timeout)
                                          .withInitialDelay(Duration.ZERO)
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

        public PatientWebConfig build() {
            return new PatientWebConfig(logConsumer,
                                        defaultWait,
                                        defaultTimeout,
                                        defaultNotPresentTimeout,
                                        defaultElementFilter,
                                        elementNotFoundExceptionBuilderFunction);
        }
    }
}
