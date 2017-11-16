package com.redfin.patient.selenium.internal;

import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientRetryHandlers;
import com.redfin.patience.PatientWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public final class PsConfig<W extends WebElement> {

    private final PatientWait defaultIsPresentWait;
    private final PatientWait defaultIsNotPresentWait;
    private final Duration defaultIsPresentTimeout;
    private final Duration defaultIsNotPresentTimeout;
    private final Predicate<W> defaultElementFilter;

    public PsConfig(PatientWait defaultIsPresentWait,
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

    public Predicate<W> getDefaultElementFilter() {
        return defaultElementFilter;
    }

    public static <W extends WebElement> PsConfigBuilder<W> builder() {
        return new PsConfigBuilder<>();
    }

    public static final class PsConfigBuilder<W extends WebElement> {

        private PatientWait defaultIsPresentWait;
        private PatientWait defaultIsNotPresentWait;
        private Duration defaultIsPresentTimeout;
        private Duration defaultIsNotPresentTimeout;
        private Predicate<W> defaultElementFilter;

        private PsConfigBuilder() {
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

        public PsConfigBuilder<W> withDefaultIsPresentWait(PatientWait wait) {
            this.defaultIsPresentWait = validate().withMessage("Cannot use a null wait.")
                                                  .that(wait)
                                                  .isNotNull();
            return this;
        }

        public PsConfigBuilder<W> withDefaultIsNotPresentWait(PatientWait wait) {
            this.defaultIsNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                                     .that(wait)
                                                     .isNotNull();
            return this;
        }

        public PsConfigBuilder<W> withDefaultIsPresentTimeout(Duration timeout) {
            this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                     .that(timeout)
                                                     .isGreaterThanOrEqualToZero();
            return this;

        }

        public PsConfigBuilder<W> withDefaultIsNotPresentTimeout(Duration timeout) {
            this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                        .that(timeout)
                                                        .isGreaterThanOrEqualToZero();
            return this;
        }

        public PsConfigBuilder<W> withDefaultElementFilter(Predicate<W> elementFilter) {
            this.defaultElementFilter = validate().withMessage("Cannot use a null element filter.")
                                                  .that(elementFilter)
                                                  .isNotNull();
            return this;
        }

        public PsConfig<W> build() {
            return new PsConfig<>(defaultIsPresentWait,
                                  defaultIsNotPresentWait,
                                  defaultIsPresentTimeout,
                                  defaultIsNotPresentTimeout,
                                  defaultElementFilter);
        }
    }
}
