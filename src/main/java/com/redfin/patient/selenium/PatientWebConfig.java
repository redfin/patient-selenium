package com.redfin.patient.selenium;

import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientRetryHandlers;
import com.redfin.patience.PatientWait;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;

import static com.redfin.validity.Validity.validate;

public final class PatientWebConfig {

    private final PatientWait defaultIsPresentWait;
    private final PatientWait defaultIsNotPresentWait;
    private final Duration defaultIsPresentTimeout;
    private final Duration defaultIsNotPresentTimeout;

    public PatientWebConfig(PatientWait defaultIsPresentWait,
                            PatientWait defaultIsNotPresentWait,
                            Duration defaultIsPresentTimeout,
                            Duration defaultIsNotPresentTimeout) {
        this.defaultIsPresentWait = validate().withMessage("Cannot use a null wait.")
                                              .that(defaultIsPresentWait)
                                              .isNotNull();
        this.defaultIsNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                                 .that(defaultIsNotPresentWait)
                                                 .isNotNull();
        this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                 .that(defaultIsPresentTimeout)
                                                 .isNotNull();
        this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                    .that(defaultIsNotPresentTimeout)
                                                    .isNotNull();
    }

    public PatientWait getDefaultIsPresentWait() {
        return defaultIsPresentWait;
    }

    public PatientWait getDefaultIsNotPresentWait() {
        return defaultIsNotPresentWait;
    }

    public Duration getDefaultIsPresentTimeout() {
        return defaultIsPresentTimeout;
    }

    public Duration getDefaultIsNotPresentTimeout() {
        return defaultIsNotPresentTimeout;
    }

    public static PatientWebConfigBuilder builder() {
        return new PatientWebConfigBuilder();
    }

    public static final class PatientWebConfigBuilder {

        private PatientWait defaultIsPresentWait;
        private PatientWait defaultIsNotPresentWait;
        private Duration defaultIsPresentTimeout;
        private Duration defaultIsNotPresentTimeout;

        public PatientWebConfigBuilder() {
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
        }

        public PatientWebConfigBuilder withDefaultIsPresentWait(PatientWait wait) {
            this.defaultIsPresentWait = validate().withMessage("Cannot use a null wait.")
                                                  .that(wait)
                                                  .isNotNull();
            return this;
        }

        public PatientWebConfigBuilder withDefaultIsNotPresentWait(PatientWait wait) {
            this.defaultIsNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                                     .that(wait)
                                                     .isNotNull();
            return this;
        }

        public PatientWebConfigBuilder withDefaultIsPresentTimeout(Duration timeout) {
            this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                     .that(timeout)
                                                     .isGreaterThanOrEqualToZero();
            return this;

        }

        public PatientWebConfigBuilder withDefaultIsNotPresentTimeout(Duration timeout) {
            this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                        .that(timeout)
                                                        .isGreaterThanOrEqualToZero();
            return this;
        }

        public PatientWebConfig build() {
            return new PatientWebConfig(defaultIsPresentWait,
                                        defaultIsNotPresentWait,
                                        defaultIsPresentTimeout,
                                        defaultIsNotPresentTimeout);
        }
    }
}
