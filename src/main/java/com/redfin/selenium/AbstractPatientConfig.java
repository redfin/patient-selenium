package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for a patient config object.
 *
 * @param <W> the type of {@link WebElement} that will be located.
 */
public abstract class AbstractPatientConfig<W extends WebElement> {

    private final Predicate<W> defaultFilter;
    private final PatientWait defaultWait;
    private final Duration defaultTimeout;
    private final int maxElementActionAttempts;
    private final Set<Class<? extends RuntimeException>> actionIgnoredExceptions;
    private final Set<Class<? extends RuntimeException>> lookupIgnoredExceptions;

    /**
     * Create a new {@link AbstractPatientConfig} instance.
     *
     * @param defaultFilter            the {@link Predicate} default element filter.
     *                                 May not be null.
     * @param defaultWait              the {@link PatientWait} default wait.
     *                                 May not be null
     * @param defaultTimeout           the {@link Duration} default timeout.
     *                                 May not be null or negative.
     * @param maxElementActionAttempts the int max number of element action attempts.
     *                                 May not be less than 1.
     * @param actionIgnoredExceptions  the set of class objects that are the types of exception to be ignored
     *                                 for element action attempts.
     *                                 May not be null. May not include {@link NoSuchElementException},
     *                                 {@link StaleElementReferenceException} or a subclass of either of those.
     * @param lookupIgnoredExceptions  the set of class object that are the types of exception to be ignored
     *                                 for element lookup attempts.
     *                                 May not be null. May not include {@link NoSuchElementException},
     *                                 {@link StaleElementReferenceException} or a subclass of either of those.
     *
     * @throws IllegalArgumentException if any argument is null, if timeout is negative, if maxElementActionAttempts
     *                                  is less than 1, or if either set of classes contains one of the invalid class types.
     */
    public AbstractPatientConfig(Predicate<W> defaultFilter,
                                 PatientWait defaultWait,
                                 Duration defaultTimeout,
                                 int maxElementActionAttempts,
                                 Set<Class<? extends RuntimeException>> actionIgnoredExceptions,
                                 Set<Class<? extends RuntimeException>> lookupIgnoredExceptions) {
        this.defaultFilter = validate().withMessage("Cannot create config with a null element filter")
                                       .that(defaultFilter)
                                       .isNotNull();
        this.defaultWait = validate().withMessage("Cannot create config with a null patient wait")
                                     .that(defaultWait)
                                     .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot create config with a null or negative default timeout")
                                        .that(defaultTimeout)
                                        .isGreaterThanOrEqualToZero();
        this.maxElementActionAttempts = validate().withMessage("Cannot create a config with a max element action attempt value that is less than 1")
                                                  .that(maxElementActionAttempts)
                                                  .isAtLeast(1);
        this.lookupIgnoredExceptions = validate().withMessage("Cannot create config with a null lookup ignored exception set")
                                                 .that(lookupIgnoredExceptions)
                                                 .isNotNull();
        this.actionIgnoredExceptions = validate().withMessage("Cannot create config with a null action ignored exception set")
                                                 .that(actionIgnoredExceptions)
                                                 .isNotNull();
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

    /**
     * @return the default element filter {@link Predicate}.
     */
    public final Predicate<W> getDefaultFilter() {
        return defaultFilter;
    }

    /**
     * @return the default {@link PatientWait} wait object.
     */
    public final PatientWait getDefaultWait() {
        return defaultWait;
    }

    /**
     * @return the default {@link Duration} timeout.
     */
    public final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @return the number of element action attempts.
     */
    public final int getMaxElementActionAttempts() {
        return maxElementActionAttempts;
    }

    /**
     * @param clazz the Class to check if is either an instance of or a super class of
     *              an ignored type.
     *              May not be null.
     *
     * @return true if clazz is an ignored action exception type.
     *
     * @throws IllegalArgumentException if clazz is null.
     */
    public final boolean isIgnoredActionException(Class<? extends RuntimeException> clazz) {
        validate().withMessage("Cannot check for a null class object")
                  .that(clazz)
                  .isNotNull();
        return actionIgnoredExceptions.stream().anyMatch(c -> c.isAssignableFrom(clazz));
    }

    /**
     * @param clazz the Class to check if is either an instance of or a super class of
     *              an ignored type.
     *              May not be null.
     *
     * @return true if clazz is an ignored lookup exception type.
     *
     * @throws IllegalArgumentException if clazz is null.
     */
    public final boolean isIgnoredLookupException(Class<? extends RuntimeException> clazz) {
        validate().withMessage("Cannot check for a null class object")
                  .that(clazz)
                  .isNotNull();
        return lookupIgnoredExceptions.stream().anyMatch(c -> c.isAssignableFrom(clazz));
    }
}
