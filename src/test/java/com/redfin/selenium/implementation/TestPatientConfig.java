package com.redfin.selenium.implementation;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.AbstractPatientConfig;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class TestPatientConfig extends AbstractPatientConfig<WebElement> {

    private TestPatientConfig(Builder builder) {
        this(builder.filter,
             builder.wait,
             builder.timeout,
             builder.maxElementActionAttempts,
             builder.actionIgnoredExceptions,
             builder.lookupIgnoredExceptions);
    }

    public TestPatientConfig(Predicate<WebElement> defaultFilter,
                             PatientWait defaultWait,
                             Duration defaultTimeout,
                             int maxElementActionAttempts,
                             Set<Class<? extends RuntimeException>> actionIgnoredExceptions,
                             Set<Class<? extends RuntimeException>> lookupIgnoredExceptions) {
        super(defaultFilter, defaultWait, defaultTimeout, maxElementActionAttempts, actionIgnoredExceptions, lookupIgnoredExceptions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Predicate<WebElement> filter = Objects::nonNull;
        private PatientWait wait = PatientWait.builder().build();
        private Duration timeout = Duration.ZERO;
        private int maxElementActionAttempts = 3;
        private Set<Class<? extends RuntimeException>> actionIgnoredExceptions = new HashSet<>();
        private Set<Class<? extends RuntimeException>> lookupIgnoredExceptions = new HashSet<>();

        public final Builder withFilter(Predicate<WebElement> filter) {
            this.filter = filter;
            return this;
        }

        public final Builder withWait(PatientWait wait) {
            this.wait = wait;
            return this;
        }

        public final Builder withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public final Builder withMaxElemementActionAttempts(int newValue) {
            this.maxElementActionAttempts = newValue;
            return this;
        }

        @SafeVarargs
        public final Builder withIgnoredActionExceptions(Class<? extends RuntimeException>... classes) {
            if (null == classes || classes.length == 0) {
                actionIgnoredExceptions = Collections.emptySet();
            } else {
                actionIgnoredExceptions = Arrays.stream(classes).collect(Collectors.toSet());
            }
            return this;
        }

        @SafeVarargs
        public final Builder withIgnoredLookupExceptions(Class<? extends RuntimeException>... classes) {
            if (null == classes || classes.length == 0) {
                lookupIgnoredExceptions = Collections.emptySet();
            } else {
                lookupIgnoredExceptions = Arrays.stream(classes).collect(Collectors.toSet());
            }
            return this;
        }

        public final TestPatientConfig build() {
            return new TestPatientConfig(this);
        }
    }
}
