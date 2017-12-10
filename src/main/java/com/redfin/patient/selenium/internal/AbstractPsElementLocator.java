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
        extends AbstractPsBase<D, W, C, P, B, THIS, E> {

    private static final String ELEMENT_FORMAT = "%s.get(%d)";
    private static final String NOT_FOUND_FORMAT = "No element found matching %s.get(%d) within %s";

    private final P driver;
    private final PatientWait wait;
    private final Duration defaultTimeout;
    private final Duration defaultNotPresentTimeout;
    private final Supplier<List<W>> elementSupplier;
    private final Predicate<W> elementFilter;

    public AbstractPsElementLocator(String description,
                                    C config,
                                    P driver,
                                    PatientWait wait,
                                    Duration defaultTimeout,
                                    Duration defaultNotPresentTimeout,
                                    Supplier<List<W>> elementSupplier,
                                    Predicate<W> elementFilter) {
        super(description, config);
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
        this.wait = validate().withMessage("Cannot use a null wait.")
                              .that(wait)
                              .isNotNull();
        this.defaultTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                        .that(defaultTimeout)
                                        .isNotNull();
        this.defaultNotPresentTimeout = validate().withMessage("Cannot use a null or negative timeout.")
                                                  .that(defaultNotPresentTimeout)
                                                  .isGreaterThanOrEqualToZero();
        this.elementSupplier = validate().withMessage("Cannot use a null element supplier.")
                                         .that(elementSupplier)
                                         .isNotNull();
        this.elementFilter = validate().withMessage("Cannot use a null element filter.")
                                       .that(elementFilter)
                                       .isNotNull();
    }

    protected abstract E buildElement(String description,
                                      W initialElement,
                                      Supplier<W> elementSupplier);

    protected final P getDriver() {
        return driver;
    }

    protected final PatientWait getWait() {
        return wait;
    }

    protected final Duration getDefaultTimeout() {
        return defaultTimeout;
    }

    protected final Duration getDefaultNotPresentTimeout() {
        return defaultNotPresentTimeout;
    }

    protected final Supplier<List<W>> getElementSupplier() {
        return elementSupplier;
    }

    protected final Predicate<W> getElementFilter() {
        return elementFilter;
    }

    public final void ifPresent(Consumer<E> consumer) {
        ifPresent(consumer, defaultTimeout);
    }

    public final void ifPresent(Consumer<E> consumer,
                                Duration timeout) {
        validate().withMessage("Cannot execute with a null consumer.")
                  .that(consumer)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            // Supply the consumer with the found element, if any
            consumer.accept(get(0, timeout));
        } catch (NoSuchElementException ignore) {
            // Do nothing
        }
    }

    public final void ifNotPresent(Runnable runnable) {
        ifNotPresent(runnable, defaultNotPresentTimeout);
    }

    public final void ifNotPresent(Runnable runnable,
                                   Duration timeout) {
        validate().withMessage("Cannot execute with a null runnable.")
                  .that(runnable)
                  .isNotNull();
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            wait.from(() -> expect().withMessage("Received a null list from the element supplier.")
                                    .that(elementSupplier.get())
                                    .isNotNull()
                                    .stream()
                                    .noneMatch(elementFilter))
                .get(timeout);
            // An empty list was found, run the runnable
            runnable.run();
        } catch (PatientTimeoutException ignore) {
            // Do nothing
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

    public final E get(int index,
                       Duration timeout) {
        validate().withMessage("Cannot locate an element with a negative index.")
                  .that(index)
                  .isAtLeast(0);
        validate().withMessage("Cannot locate an element with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        Supplier<W> elementSupplier = createElementSupplier(index, timeout);
        return buildElement(String.format(ELEMENT_FORMAT,
                                          this,
                                          index),
                            elementSupplier.get(),
                            elementSupplier);
    }

    public final List<E> getAll() {
        return getAll(defaultTimeout);
    }

    public final List<E> getAll(Duration timeout) {
        validate().withMessage("Cannot locate elements with a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        try {
            List<W> elements = wait.from(() -> expect().withMessage("Received a null list from the element supplier.")
                                                       .that(elementSupplier.get())
                                                       .isNotNull()
                                                       .stream()
                                                       .filter(elementFilter)
                                                       .collect(Collectors.toList()))
                                   .withFilter(list -> null != list && !list.isEmpty())
                                   .get(timeout);
            List<E> builtElements = new ArrayList<>(elements.size());
            for (int i = 0; i < elements.size(); i++) {
                builtElements.add(buildElement(String.format(ELEMENT_FORMAT,
                                                             this,
                                                             i),
                                               elements.get(i),
                                               createElementSupplier(i, timeout)));
            }
            return builtElements;
        } catch (PatientTimeoutException ignore) {
            return Collections.emptyList();
        }
    }

    private Supplier<W> createElementSupplier(int index,
                                              Duration timeout) {
        return () -> {
            try {
                return wait.from(() -> {
                    List<W> list = expect().withMessage("Received a null list from the element supplier.")
                                           .that(elementSupplier.get())
                                           .isNotNull()
                                           .stream()
                                           .filter(elementFilter)
                                           .limit(index + 1)
                                           .collect(Collectors.toList());
                    if (list.size() >= index + 1) {
                        return list.get(index);
                    } else {
                        return null;
                    }
                }).get(timeout);
            } catch (PatientTimeoutException ignore) {
                throw getConfig().getElementNotFoundExceptionBuilderFunction()
                                 .apply(String.format(NOT_FOUND_FORMAT,
                                                      this,
                                                      index,
                                                      timeout));
            }
        };
    }
}
