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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElementLocator<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, B, THIS, E>,
        P extends AbstractPsDriver<D, W, C, P, B, THIS, E>,
        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, THIS, E>,
        THIS extends AbstractPsElementLocator<D, W, C, P, B, THIS, E>,
        E extends AbstractPsElement<D, W, C, P, B, THIS, E>>
        extends AbstractPsBase<W, C, B, THIS, E>
        implements PsElementLocator<W, C, B, THIS, E> {

    private static final String FORMAT = "%s.get(%d)";

    private final PatientWait wait;
    private final Duration defaultTimeout;
    private final Duration defaultAssertNotPresentTimeout;
    private final Supplier<List<W>> elementSupplier;
    private final Predicate<W> elementFilter;
    private final P driver;

    public AbstractPsElementLocator(String description,
                                    C config,
                                    PatientWait wait,
                                    Duration defaultTimeout,
                                    Duration defaultAssertNotPresentTimeout,
                                    Supplier<List<W>> elementSupplier,
                                    Predicate<W> elementFilter,
                                    P driver) {
        super(description, config);
        this.wait = validate().withMessage("Cannot use a null wait.")
                              .that(wait)
                              .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                        .that(defaultTimeout)
                                        .isNotNull();
        this.defaultAssertNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                        .that(defaultAssertNotPresentTimeout)
                                                        .isGreaterThanOrEqualToZero();
        this.elementSupplier = validate().withMessage("Cannot use a null element supplier.")
                                         .that(elementSupplier)
                                         .isNotNull();
        this.elementFilter = validate().withMessage("Cannot use a null element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
    }

    protected final PatientWait getWait() {
        return wait;
    }

    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    protected final Duration getDefaultAssertNotPresentTimeout() {
        return defaultAssertNotPresentTimeout;
    }

    protected final Supplier<List<W>> getElementSupplier() {
        return elementSupplier;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    protected final P getDriver() {
        return driver;
    }

    private Supplier<W> createElementSupplier(int index,
                                              Duration timeout) {
        return () -> {
            try {
                // Wait until there are index + 1 matching elements
                List<W> foundElements = wait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
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
            return wait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
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
        return buildElement(elementDescription,
                            elementSupplier.get(),
                            elementSupplier);
    }

    protected abstract E buildElement(String elementDescription,
                                      W initialElement,
                                      Supplier<W> elementSupplier);

    @Override
    public final boolean isPresent() {
        return isPresent(defaultTimeout);
    }

    @Override
    public final boolean isPresent(Duration timeout) {
        try {
            get(0, timeout).withWrappedElement().accept(e -> {
            });
            return true;
        } catch (NoSuchElementException ignore) {
            return false;
        }
    }

    @Override
    public final void ifPresent(Consumer<E> consumer) {
        ifPresent(consumer, defaultTimeout);
    }

    @Override
    public final void ifPresent(Consumer<E> consumer,
                                Duration timeout) {
        validate().withMessage("Cannot use a null consumer.")
                  .that(consumer)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            E element = get(0, timeout);
            element.withWrappedElement().accept(e -> {
            });
            consumer.accept(element);
        } catch (NoSuchElementException ignore) {
            // do nothing
        }
    }

    @Override
    public final boolean isNotPresent() {
        return isNotPresent(defaultAssertNotPresentTimeout);
    }

    @Override
    public final boolean isNotPresent(Duration timeout) {
        try {
            assertNotPresent(timeout);
            // No error means the element is not present
            return true;
        } catch (AssertionError ignore) {
            // Assertion thrown means the element is present
            return false;
        }
    }

    @Override
    public final void assertNotPresent() {
        assertNotPresent(defaultAssertNotPresentTimeout, null);
    }

    @Override
    public final void assertNotPresent(String failureMessage) {
        assertNotPresent(defaultAssertNotPresentTimeout, failureMessage);
    }

    @Override
    public final void assertNotPresent(Duration timeout) {
        assertNotPresent(timeout, null);
    }

    @Override
    public final void assertNotPresent(Duration timeout,
                                       String failureMessage) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            // Wait until no elements match
            wait.from(() -> expect().withMessage("Received a null selenium element list from the supplier.")
                                    .that(elementSupplier.get())
                                    .isNotNull()
                                    .stream()
                                    .noneMatch(elementFilter))
                .get(timeout);
        } catch (PatientTimeoutException ignore) {
            // Timeout reached without getting an empty list
            if (null == failureMessage || failureMessage.isEmpty()) {
                throw new AssertionError(String.format("Expected to not find an element matching %s after waiting %s",
                                                       this,
                                                       timeout));
            } else {
                throw new AssertionError(failureMessage);
            }
        }
    }

    @Override
    public final E get() {
        return get(0, defaultTimeout);
    }

    @Override
    public final E get(Duration timeout) {
        return get(0, timeout);
    }

    @Override
    public final E get(int index) {
        return get(index, defaultTimeout);
    }

    @Override
    public final E get(int index,
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
    public final List<E> getAll() {
        return getAll(defaultTimeout);
    }

    @Override
    public final List<E> getAll(Duration timeout) {
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
