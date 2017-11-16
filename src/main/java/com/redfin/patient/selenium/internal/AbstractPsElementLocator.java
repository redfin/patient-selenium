package com.redfin.patient.selenium.internal;

import com.redfin.patience.PatientTimeoutException;
import com.redfin.patience.PatientWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElementLocator<W extends WebElement,
        E extends AbstractPsElement<W>> {

    private static final String FORMAT = "%s.get(%d)";

    private final String elementLocationDescription;
    private final Supplier<List<W>> seleniumElementSupplier;
    private final Predicate<W> elementFilter;
    private final PatientWait isPresentWait;
    private final PatientWait isNotPresentWait;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;
    private final PsConfig<W> config;

    public AbstractPsElementLocator(String elementLocationDescription,
                                    Supplier<List<W>> seleniumElementSupplier,
                                    Predicate<W> elementFilter,
                                    PatientWait isPresentWait,
                                    PatientWait isNotPresentWait,
                                    Duration defaultTimeout,
                                    Duration defaultNotPresentTimeout,
                                    PsConfig<W> config) {
        this.elementLocationDescription = validate().withMessage("Cannot use a null or empty element location description.")
                                                    .that(elementLocationDescription)
                                                    .isNotEmpty();
        this.seleniumElementSupplier = validate().withMessage("Cannot use a null selenium element supplier.")
                                                 .that(seleniumElementSupplier)
                                                 .isNotNull();
        this.elementFilter = validate().withMessage("Cannot use a null selenium element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
        this.isPresentWait = validate().withMessage("Cannot use a null wait.")
                                       .that(isPresentWait)
                                       .isNotNull();
        this.isNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                          .that(isNotPresentWait)
                                          .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative default timeout.")
                                        .that(defaultTimeout)
                                        .isGreaterThanOrEqualToZero();
        this.defaultNotPresentTimeout = validate().withMessage("Cannot use a null or negative default not present timeout.")
                                                  .that(defaultNotPresentTimeout)
                                                  .isGreaterThanOrEqualToZero();
        this.config = validate().withMessage("Cannot use a null config.")
                                .that(config)
                                .isNotNull();
    }

    protected final PsConfig<W> getConfig() {
        return config;
    }

    private Supplier<W> buildElementSupplier(int index,
                                             Duration timeout) {
        return () -> {
            try {
                // Wait until index + 1 matching elements are found
                List<W> foundElements = isPresentWait.from(() -> expect().withMessage("Received a null element list from the selenium element supplier")
                                                                         .that(seleniumElementSupplier.get())
                                                                         .isNotNull()
                                                                         .stream()
                                                                         .filter(elementFilter)
                                                                         .limit(index + 1)
                                                                         .collect(Collectors.toList()))
                                                     .withFilter(l -> null != l && !l.isEmpty() && l.size() > index)
                                                     .get(timeout);
                return foundElements.get(index);
            } catch (PatientTimeoutException exception) {
                // Never found index + 1 matching elements
                throw new NoSuchElementException("No matching element found at index " + index + " for " + elementLocationDescription);
            }
        };
    }

    private ElementResult<W> getElementResult(int index,
                                              Duration timeout) {
        Supplier<W> foundElementSupplier = buildElementSupplier(index, timeout);
        W initialElement = foundElementSupplier.get();
        return new ElementResult<>(String.format(FORMAT,
                                                 elementLocationDescription,
                                                 index),
                                   initialElement,
                                   foundElementSupplier);
    }

    private List<ElementResult<W>> getAllElementResults(Duration timeout) {
        try {
            // Wait until the matching element list is not empty
            List<W> foundElements = isPresentWait.from(() -> expect().withMessage("Received a null element list from the selenium element supplier.")
                                                                     .that(seleniumElementSupplier.get())
                                                                     .isNotNull()
                                                                     .stream()
                                                                     .filter(elementFilter)
                                                                     .collect(Collectors.toList()))
                                                 .withFilter(l -> null != l && !l.isEmpty())
                                                 .get(timeout);
            // Build and return the list of element result objects
            List<ElementResult<W>> results = new ArrayList<>(foundElements.size());
            for (int i = 0; i < foundElements.size(); i++) {
                results.add(new ElementResult<>(String.format(FORMAT,
                                                              elementLocationDescription,
                                                              i),
                                                foundElements.get(i),
                                                buildElementSupplier(i, timeout)));
            }
            return results;
        } catch (PatientTimeoutException ignore) {
            // No matching elements were found within the timeout duration
            return Collections.emptyList();
        }
    }

    protected abstract E buildElement(String elementDescription,
                                      W initialElement,
                                      Supplier<W> elementSupplier);

    public final boolean isPresent() {
        return isPresent(defaultTimeout);
    }

    public final boolean isPresent(Duration timeout) {
        try {
            get(0, timeout);
            return true;
        } catch (NoSuchElementException ignore) {
            return false;
        }
    }

    public final boolean isNotPresent() {
        return isNotPresent(defaultNotPresentTimeout);
    }

    public final boolean isNotPresent(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            // Wait until the returned matching element list is empty.
            isNotPresentWait.from(() -> expect().withMessage("Received a null element list from the selenium element supplier.")
                                                .that(seleniumElementSupplier.get())
                                                .isNotNull()
                                                .stream()
                                                .filter(elementFilter)
                                                .collect(Collectors.toList()))
                            .withFilter(l -> null != l && l.isEmpty())
                            .get(timeout);
            // The list is empty
            return true;
        } catch (PatientTimeoutException ignore) {
            // The list was never empty
            return false;
        }
    }

    public final E get() {
        return get(0, defaultTimeout);
    }

    public final E get(Duration timeout) {
        return get(0, timeout);
    }

    public final E get(int index) {
        return get(index, defaultTimeout);
    }

    public final E get(int index, Duration timeout) {
        validate().withMessage("Cannot use a negative index.")
                  .that(index)
                  .isAtLeast(0);
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        ElementResult<W> result = getElementResult(index, timeout);
        return buildElement(result.getElementDescription(),
                            result.getInitialElement(),
                            result.getElementSupplier());
    }

    public final List<E> getAll() {
        return getAll(defaultTimeout);
    }

    public final List<E> getAll(Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        return getAllElementResults(timeout).stream()
                                            .map(er -> buildElement(er.getElementDescription(),
                                                                    er.getInitialElement(),
                                                                    er.getElementSupplier()))
                                            .collect(Collectors.toList());
    }

    private static final class ElementResult<W extends WebElement> {

        private final String elementDescription;
        private final W initialElement;
        private final Supplier<W> elementSupplier;

        private ElementResult(String elementDescription,
                              W initialElement,
                              Supplier<W> elementSupplier) {
            this.elementDescription = elementDescription;
            this.initialElement = initialElement;
            this.elementSupplier = elementSupplier;
        }

        private String getElementDescription() {
            return elementDescription;
        }

        private W getInitialElement() {
            return initialElement;
        }

        private Supplier<W> getElementSupplier() {
            return elementSupplier;
        }
    }
}
