package com.redfin.selenium;

import com.redfin.patience.DelaySuppliers;
import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.redfin.validity.Validity.validate;

public final class PatientSeleniumConfig<W extends WebElement> {

    private final Predicate<W> defaultFilter;
    private final PatientWait defaultWait;
    private final Duration defaultTimeout;
    private final int maxElementActionAttempts;
    private final Set<Class<? extends RuntimeException>> actionIgnoredExceptions;
    private final Set<Class<? extends RuntimeException>> lookupIgnoredExceptions;

    private PatientSeleniumConfig(Builder<W> builder) {
        validate().that(builder).isNotNull();
        this.defaultFilter = validate().withMessage("Cannot create config with a null element filter")
                                       .that(builder.filter)
                                       .isNotNull();
        this.defaultWait = validate().withMessage("Cannot create config with a null patient wait")
                                     .that(builder.wait)
                                     .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot create config with a null or negative default timeout")
                                        .that(builder.timeout)
                                        .isGreaterThanOrEqualToZero();
        this.lookupIgnoredExceptions = validate().withMessage("Cannot create config with a null lookup ignored exception set")
                                                 .that(builder.lookupIgnoredExceptions)
                                                 .isNotNull();
        this.actionIgnoredExceptions = validate().withMessage("Cannot create config with a null action ignored exception set")
                                                 .that(builder.actionIgnoredExceptions)
                                                 .isNotNull();
        this.maxElementActionAttempts = validate().withMessage("Cannot create a config with a max element action attempt value that is less than 1")
                                                  .that(builder.maxElementActionAttempts)
                                                  .isAtLeast(1);
        if (actionIgnoredExceptions.stream().anyMatch(NoSuchElementException.class::equals)) {
            throw new IllegalArgumentException("Cannot have actions ignore org.openqa.selenium.NoSuchElementException as that exception type is handled explicitly");
        }
        if (actionIgnoredExceptions.stream().anyMatch(StaleElementReferenceException.class::equals)) {
            throw new IllegalArgumentException("Cannot have actions ignore org.openqa.selenium.StaleElementReferenceException as that exception type is handled explicitly");
        }
        if (lookupIgnoredExceptions.stream().anyMatch(NoSuchElementException.class::equals)) {
            throw new IllegalArgumentException("Cannot have element look-ups ignore org.openqa.selenium.NoSuchElementException as that exception type is handled explicitly");
        }
        if (lookupIgnoredExceptions.stream().anyMatch(StaleElementReferenceException.class::equals)) {
            throw new IllegalArgumentException("Cannot have element look-ups ignore org.openqa.selenium.StaleElementReferenceException as that exception type is handled explicitly");
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Predicate<W> getDefaultFilter() {
        return defaultFilter;
    }

    public PatientWait getDefaultWait() {
        return defaultWait;
    }

    public Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    public int getMaxElementActionAttempts() {
        return maxElementActionAttempts;
    }

    public boolean isIgnoredActionException(Class<? extends RuntimeException> clazz) {
        validate().withMessage("Cannot check for a null class object")
                  .that(clazz)
                  .isNotNull();
        return actionIgnoredExceptions.stream().anyMatch(c -> c.isAssignableFrom(clazz));
    }

    public boolean isIgnoredLookupException(Class<? extends RuntimeException> clazz) {
        validate().withMessage("Cannot check for a null class object")
                  .that(clazz)
                  .isNotNull();
        return lookupIgnoredExceptions.stream().anyMatch(c -> c.isAssignableFrom(clazz));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Builder object
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static <W extends WebElement> Builder<W> builder() {
        return new Builder<>();
    }

    public static final class Builder<W extends WebElement> {

        private Predicate<W> filter = Objects::nonNull;
        private PatientWait wait = PatientWait.builder()
                                              .withInitialDelay(Duration.ZERO)
                                              .withDelaySupplier(DelaySuppliers.fixed(Duration.ofMillis(500)))
                                              .withExecutionHandler(PatientExecutionHandlers.simple())
                                              .build();
        private Duration timeout = Duration.ofSeconds(30);
        private int maxElementActionAttempts = 3;
        private Set<Class<? extends RuntimeException>> actionIgnoredExceptions = Stream.of(WebDriverException.class)
                                                                                       .collect(Collectors.toSet());
        private Set<Class<? extends RuntimeException>> lookupIgnoredExceptions = new HashSet<>();

        public final Builder<W> withFilter(Predicate<W> filter) {
            this.filter = filter;
            return this;
        }

        public final Builder<W> withWait(PatientWait wait) {
            this.wait = wait;
            return this;
        }

        public final Builder<W> withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public final Builder<W> withMaxElemementActionAttempts(int maxElementActionAttempts) {
            this.maxElementActionAttempts = maxElementActionAttempts;
            return this;
        }

        @SafeVarargs
        public final Builder<W> withIgnoredActionExceptions(Class<? extends RuntimeException>... exceptionsToIgnore) {
            actionIgnoredExceptions = ignoredExceptionHelper(exceptionsToIgnore);
            return this;
        }

        @SafeVarargs
        public final Builder<W> withIgnoredLookupExceptions(Class<? extends RuntimeException>... exceptionsToIgnore) {
            lookupIgnoredExceptions = ignoredExceptionHelper(exceptionsToIgnore);
            return this;
        }

        @SafeVarargs
        private final Set<Class<? extends RuntimeException>> ignoredExceptionHelper(Class<? extends RuntimeException>... exceptionsToIgnore) {
            if (null == exceptionsToIgnore || exceptionsToIgnore.length == 0) {
                return Collections.emptySet();
            }
            return Arrays.stream(exceptionsToIgnore)
                         .collect(Collectors.toSet());
        }

        public final PatientSeleniumConfig<W> build() {
            return new PatientSeleniumConfig<>(this);
        }
    }
}
