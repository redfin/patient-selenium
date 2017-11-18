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

package com.redfin.patient.selenium.internal;

import com.redfin.patience.PatientTimeoutException;
import com.redfin.patience.PatientWait;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElementLocator<W extends WebElement,
        C extends PsConfig<W, C, B, THIS, E>,
        B extends PsElementLocatorBuilder<W, C, B, THIS, E>,
        THIS extends AbstractPsElementLocator<W, C, B, THIS, E>,
        E extends PsElement<W, C, B, THIS, E>>
        extends AbstractPsBase<W, C, B, THIS, E>
        implements PsElementLocator<W, C, B, THIS, E> {

    private static final String FORMAT = "%s.get(%d)";

    private final PatientWait isPresentWait;
    private final PatientWait isNotPresentWait;
    private final Duration isPresentTimeout;
    private final Duration isNotPresentTimeout;
    private final Supplier<List<W>> elementSupplier;
    private final Predicate<W> elementFilter;

    public AbstractPsElementLocator(String description,
                                    C config,
                                    PatientWait isPresentWait,
                                    PatientWait isNotPresentWait,
                                    Duration isPresentTimeout,
                                    Duration isNotPresentTimeout,
                                    Supplier<List<W>> elementSupplier,
                                    Predicate<W> elementFilter) {
        super(description, config);
        this.isPresentWait = validate().withMessage("Cannot use a null wait.")
                                       .that(isPresentWait)
                                       .isNotNull();
        this.isNotPresentWait = validate().withMessage("Cannot use a null wait.")
                                          .that(isNotPresentWait)
                                          .isNotNull();
        this.isPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                          .that(isPresentTimeout)
                                          .isGreaterThanOrEqualToZero();
        this.isNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                             .that(isNotPresentTimeout)
                                             .isGreaterThanOrEqualToZero();
        this.elementSupplier = validate().withMessage("Cannot use a null element supplier.")
                                         .that(elementSupplier)
                                         .isNotNull();
        this.elementFilter = validate().withMessage("Cannot use a null element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
    }

    protected final PatientWait getIsPresentWait() {
        return isPresentWait;
    }

    protected final PatientWait getIsNotPresentWait() {
        return isNotPresentWait;
    }

    protected final Duration getIsPresentTimeout() {
        return isPresentTimeout;
    }

    protected final Duration isNotPresentTimeout() {
        return isNotPresentTimeout;
    }

    protected final Supplier<List<W>> getElementSupplier() {
        return elementSupplier;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    private Supplier<W> createElementSupplier(int index,
                                              Duration timeout) {
        return () -> {
            try {
                // Wait until there are index + 1 matching elements
                List<W> foundElements = isPresentWait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
                                                                         .that(elementSupplier.get())
                                                                         .isNotNull()
                                                                         .stream()
                                                                         .filter(elementFilter)
                                                                         .limit(index + 1)
                                                                         .collect(Collectors.toList()))
                                                     .withFilter(list -> null != list && !list.isEmpty() && list.size() > index)
                                                     .get(timeout);
                return foundElements.get(index);
            } catch (PatientTimeoutException exception) {
                // Never found index + 1 matching elements within the timeout, throw exception
                throw new NoSuchElementException(String.format("Didn't find at least %d element within %s that matched %s",
                                                               index + 1,
                                                               timeout,
                                                               getDescription()));
            }
        };
    }

    private List<W> getAllElements(Duration timeout) {
        try {
            return isPresentWait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
                                                    .that(elementSupplier.get())
                                                    .isNotNull()
                                                    .stream()
                                                    .filter(elementFilter)
                                                    .collect(Collectors.toList()))
                                .withFilter(list -> null != list && !list.isEmpty())
                                .get(timeout);
        } catch (PatientTimeoutException ignore) {
            // No matching elements found within the timeout, return empty list
            return Collections.emptyList();
        }
    }

    protected final E buildElement(String elementDescription,
                                   Supplier<W> elementSupplier) {
        return buildElement(elementDescription, null, elementSupplier);
    }

    protected abstract E buildElement(String elementDescription,
                                      W initialElement,
                                      Supplier<W> elementSupplier);

    @Override
    public boolean isPresent() {
        return isPresent(isPresentTimeout);
    }

    @Override
    public boolean isPresent(Duration timeout) {
        try {
            get(0, timeout).withWrappedElement().accept(e -> { });
            return true;
        } catch (NoSuchElementException ignore) {
            return false;
        }
    }

    @Override
    public boolean isNotPresent() {
        return isNotPresent(isNotPresentTimeout);
    }

    @Override
    public boolean isNotPresent(Duration timeout) {
        try {
            // Wait until no elements match
            isNotPresentWait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
                                                .that(elementSupplier.get())
                                                .isNotNull()
                                                .stream()
                                                .noneMatch(elementFilter))
                            .get(timeout);
            return true;
        } catch (PatientTimeoutException ignore) {
            // Timeout reached without getting an empty list
            return false;
        }
    }

    @Override
    public E get() {
        return get(0, isPresentTimeout);
    }

    @Override
    public E get(Duration timeout) {
        return get(0, timeout);
    }

    @Override
    public E get(int index) {
        return get(index, isPresentTimeout);
    }

    @Override
    public E get(int index,
                 Duration timeout) {
        validate().withMessage("Cannot get an element with a negative index.")
                  .that(index)
                  .isAtLeast(0);
        validate().withMessage("Cannot get an element with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        Supplier<W> elementSupplier = createElementSupplier(index, timeout);
        return buildElement(String.format(FORMAT,
                                          getDescription(),
                                          index),
                            elementSupplier);
    }

    @Override
    public List<E> getAll() {
        return getAll(isPresentTimeout);
    }

    @Override
    public List<E> getAll(Duration timeout) {
        validate().withMessage("Cannot get elements with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        List<W> foundElements = getAllElements(timeout);
        List<E> elements = new ArrayList<>(foundElements.size());
        for (int i = 0; i < foundElements.size(); i++) {
            elements.add(buildElement(String.format(FORMAT,
                                                    getDescription(),
                                                    i),
                                      foundElements.get(i),
                                      createElementSupplier(i, timeout)));
        }
        return elements;
    }
}
