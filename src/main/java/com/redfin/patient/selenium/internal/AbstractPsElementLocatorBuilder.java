package com.redfin.patient.selenium.internal;

import com.redfin.patience.PatientWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElementLocatorBuilder<W extends WebElement,
        THIS extends AbstractPsElementLocatorBuilder<W, THIS, L, E>,
        L extends AbstractPsElementLocator<W, E>,
        E extends AbstractPsElement<W>> {

    private PatientWait isPresentWait;
    private PatientWait isNotPresentWait;
    private Duration defaultIsPresentTimeout;
    private Duration defaultIsNotPresentTimeout;
    private Predicate<W> elementFilter = Objects::nonNull;

    public AbstractPsElementLocatorBuilder(PatientWait isPresentWait,
                                           PatientWait isNotPresentWait,
                                           Duration defaultIsPresentTimeout,
                                           Duration defaultIsNotPresentTimeout) {
        this.isPresentWait = validate().withMessage("Cannot use a null wait.")
                                       .that(isPresentWait)
                                       .isNotNull();
        this.isNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                          .that(isNotPresentWait)
                                          .isNotNull();
        this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                 .that(defaultIsPresentTimeout)
                                                 .isGreaterThanOrEqualToZero();
        this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                    .that(defaultIsNotPresentTimeout)
                                                    .isGreaterThanOrEqualToZero();

    }

    protected final PatientWait getIsPresentWait() {
        return isPresentWait;
    }

    protected final PatientWait getIsNotPresentWait() {
        return isNotPresentWait;
    }

    protected final Duration getDefaultIsPresentTimeout() {
        return defaultIsPresentTimeout;
    }

    protected final Duration getDefaultIsNotPresentTimeout() {
        return defaultIsNotPresentTimeout;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    protected abstract THIS getThis();

    public abstract L by(By by);

    public THIS withIsPresentWait(PatientWait wait) {
        this.isPresentWait = validate().withMessage("Cannot use a null wait.")
                                       .that(wait)
                                       .isNotNull();
        return getThis();
    }

    public THIS withIsNotPresentWait(PatientWait wait) {
        this.isNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                          .that(wait)
                                          .isNotNull();
        return getThis();
    }

    public THIS withDefaultTimeout(Duration timeout) {
        this.defaultIsPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                 .that(timeout)
                                                 .isGreaterThanOrEqualToZero();
        return getThis();
    }

    public THIS withDefaultIsNotPresentTimeout(Duration timeout) {
        this.defaultIsNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                    .that(timeout)
                                                    .isGreaterThanOrEqualToZero();
        return getThis();
    }

    public THIS withFilter(Predicate<W> elementFilter) {
        this.elementFilter = validate().withMessage("Cannot use a null element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
        return getThis();
    }
}
